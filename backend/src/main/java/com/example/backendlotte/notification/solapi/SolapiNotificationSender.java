package com.example.backendlotte.notification.solapi;

import org.springframework.stereotype.Component;

import com.example.backendlotte.notification.dto.NotificationSendResult;
import com.example.backendlotte.notification.service.NotificationSender;
import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolapiNotificationSender
        implements NotificationSender {

    private final SolapiProperties properties;

    @Override
    public NotificationSendResult send(
            String recipient,
            String messageText
    ) {
        validateConfiguration();

        DefaultMessageService messageService =
            SolapiClient.INSTANCE.createInstance(
                properties.apiKey(),
                properties.apiSecret()
            );

        Message message = new Message();

        message.setFrom(
            normalizePhone(properties.sender())
        );

        message.setTo(
            normalizePhone(recipient)
        );

        message.setText(messageText);

        try {
            messageService.send(message);

            return new NotificationSendResult(
                "SOLAPI",
                null
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                "SOLAPI 문자 발송에 실패했습니다.",
                exception
            );
        }
    }

    private void validateConfiguration() {

        if (properties.apiKey() == null
                || properties.apiKey().isBlank()) {

            throw new IllegalStateException(
                "SOLAPI API KEY가 설정되어 있지 않습니다."
            );
        }

        if (properties.apiSecret() == null
                || properties.apiSecret().isBlank()) {

            throw new IllegalStateException(
                "SOLAPI API SECRET이 설정되어 있지 않습니다."
            );
        }

        if (properties.sender() == null
                || properties.sender().isBlank()) {

            throw new IllegalStateException(
                "SOLAPI 발신번호가 설정되어 있지 않습니다."
            );
        }
    }

    private String normalizePhone(
            String phone
    ) {
        return phone.replaceAll(
            "[^0-9]",
            ""
        );
    }
}