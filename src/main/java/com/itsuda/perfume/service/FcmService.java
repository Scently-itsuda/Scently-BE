package com.itsuda.perfume.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final ObjectMapper objectMapper;

    public void sendFCMMessage(String title, String body, String token) throws FirebaseMessagingException {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        // 후에 비동기 메시징으로 수정
        firebaseMessaging.sendAsync(Message.builder().setToken(token).setNotification(notification).build());
    }

    public void sendFCMMessage(String title, String body, List<String> tokens) throws FirebaseMessagingException {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        // 후에 비동기 메시징으로 수정
        tokens.forEach(token -> firebaseMessaging.sendAsync(Message.builder().setToken(token)
                .setNotification(notification)
                .build()));
    }
}
