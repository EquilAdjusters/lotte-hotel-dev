package com.example.backendlotte.account.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.dto.LoginAccessLogIntegrityResponse;
import com.example.backendlotte.account.entity.LoginAccessLog;
import com.example.backendlotte.account.repository.LoginAccessLogRepository;

import lombok.RequiredArgsConstructor;

/*
 * 접속기록 위·변조 방지 조치.
 * login_access_logs는 매 기록마다 직전 기록의 recordHash를 물려받아
 * 자신의 recordHash를 계산하는 해시체인으로 저장된다 (LoginAccessLog 참조).
 * 이 서비스는 그 체인을 처음부터 끝까지 다시 계산해서
 * DB에 저장된 값과 다른 곳(=중간에 값이 바뀐 곳)이 있는지 확인한다.
 */
@Service
@RequiredArgsConstructor
public class LoginAccessLogIntegrityService {

    private final LoginAccessLogRepository loginAccessLogRepository;

    @Transactional(readOnly = true)
    public LoginAccessLogIntegrityResponse verify() {
        List<LoginAccessLog> logs =
            loginAccessLogRepository.findAllByOrderByIdAsc();

        String expectedPreviousHash = null;
        int checkedCount = 0;
        List<Long> tamperedIds = new ArrayList<>();
        List<Long> forkedIds = new ArrayList<>();

        for (LoginAccessLog logRow : logs) {

            if (LoginAccessLog.LEGACY_UNCHAINED.equals(
                    logRow.getRecordHash())) {
                continue;
            }

            checkedCount++;

            String recomputed = LoginAccessLog.computeHash(
                logRow.getPreviousHash(),
                logRow.getAccount() != null
                    ? logRow.getAccount().getId()
                    : null,
                logRow.getAttemptedLoginId(),
                logRow.isSuccess(),
                logRow.getFailureReason(),
                logRow.getIpAddress(),
                logRow.getUserAgent(),
                logRow.getSessionId(),
                logRow.getLoginAt()
            );

            if (!recomputed.equals(logRow.getRecordHash())) {
                tamperedIds.add(logRow.getId());
            }

            // 동시 로그인 시도로 같은 previousHash에서 두 기록이 갈라질 수 있다.
            // 이 자체가 위·변조는 아니지만 사람이 확인해 볼 필요는 있어 별도로 남긴다.
            if (expectedPreviousHash != null
                    && !expectedPreviousHash.equals(logRow.getPreviousHash())) {
                forkedIds.add(logRow.getId());
            }

            expectedPreviousHash = logRow.getRecordHash();
        }

        return new LoginAccessLogIntegrityResponse(
            tamperedIds.isEmpty(),
            checkedCount,
            tamperedIds,
            forkedIds
        );
    }
}
