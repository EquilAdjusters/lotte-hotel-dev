package com.example.backendlotte.claim.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.claim.entity.ClaimNumberSequence;
import com.example.backendlotte.claim.repository.ClaimNumberSequenceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClaimNumberGenerator {

    private static final DateTimeFormatter PERIOD_FORMATTER =
        DateTimeFormatter.ofPattern("yyMM");

    private final ClaimNumberSequenceRepository sequenceRepository;
    private final Clock clock;

    @Transactional
    public String generate() {
        String period = LocalDate
            .now(clock)
            .format(PERIOD_FORMATTER);

        ClaimNumberSequence sequence =
            findOrCreateSequence(period);

        int nextSequence =
            sequence.nextSequence();

        return "%s-%04d".formatted(
            period,
            nextSequence
        );
    }

    private ClaimNumberSequence findOrCreateSequence(
            String period
    ) {
        return sequenceRepository
            .findByPeriodForUpdate(period)
            .orElseGet(() ->
                createSequence(period)
            );
    }

    private ClaimNumberSequence createSequence(
            String period
    ) {
        try {
            ClaimNumberSequence sequence =
                ClaimNumberSequence.create(period);

            sequenceRepository.saveAndFlush(sequence);

            return sequenceRepository
                .findByPeriodForUpdate(period)
                .orElseThrow(() ->
                    new IllegalStateException(
                        "접수번호 생성 정보를 찾을 수 없습니다."
                    )
                );

        } catch (DataIntegrityViolationException exception) {
            /*
             * 같은 월의 첫 번째 접수가 동시에 들어오면
             * 두 요청이 period 행 생성을 시도할 수 있다.
             * 한 요청만 성공하고 나머지는 중복 키 예외가 발생하므로
             * 이미 생성된 행을 다시 잠금 조회한다.
             */
            return sequenceRepository
                .findByPeriodForUpdate(period)
                .orElseThrow(() ->
                    new IllegalStateException(
                        "접수번호 생성 중 충돌이 발생했습니다."
                    )
                );
        }
    }
}