package com.itsuda.perfume.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final ObjectMapper objectMapper;

    @Async
    public void sendFCMMessage(String title, String body, String token) {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        firebaseMessaging.sendAsync(Message.builder().setToken(token).setNotification(notification).build());
    }

    @Async
    public void sendFCMMessage(String title, String body, List<String> tokens) {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        tokens.forEach(token -> firebaseMessaging.sendAsync(Message.builder().setToken(token)
                .setNotification(notification)
                .build()));
    }

    private void valid_message(String title, String body) {
        if (!StringUtils.hasText(title) || !StringUtils.hasText(body)) {
            throw new RestApiException(ErrorCode.INVALID_FCM_MESSAGE);
        }
    }
}
