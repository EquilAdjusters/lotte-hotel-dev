package com.example.backendlotte.notification.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.backendlotte.notification.dto.NotificationSendResult;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME:}")
    private String fromAddress;

    @Value("${MAIL_FROM_NAME:와이즈보험중개}")
    private String fromName;

    @Override
    public NotificationSendResult send(
            String to,
            String subject,
            String body
    ) {
        if (fromAddress == null
                || fromAddress.isBlank()) {
            throw new IllegalStateException(
                "메일 발송 계정(MAIL_USERNAME)이 설정되어 있지 않습니다."
            );
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                false,
                "UTF-8"
            );

            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(mimeMessage);

            return new NotificationSendResult(
                "SMTP",
                null
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                "이메일 발송에 실패했습니다.",
                exception
            );
        }
    }
}
