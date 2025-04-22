package com.itsuda.perfume.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserFcmToken;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.UserFcmTokenRepository;
import com.itsuda.perfume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.itsuda.perfume.exception.ErrorCode.INVALID_FCM_MESSAGE;
import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;

    @Transactional
    public void saveUserFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        userFcmTokenRepository.save(UserFcmToken.builder().user(user).fcmToken(fcmToken).build());
    }

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
            throw new RestApiException(INVALID_FCM_MESSAGE);
        }
    }
}
