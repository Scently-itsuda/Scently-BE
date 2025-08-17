package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Notification;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.NotificationType;
import com.itsuda.perfume.dto.response.notification.UserNotificationsDto;
import com.itsuda.perfume.repository.NotificationRepository;
import com.itsuda.perfume.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;


    @DisplayName("사용자 ID에 해당하는 사용자의 모든 알림들을 날짜 내림차순으로 조회할 수 있다.")
    @Test
    void getAllUserNotificationsByUserId() {
        // given
        User sender = userRepository.save(createTestUser(1));
        User receiver = userRepository.save(createTestUser(2));

        setMockingTime(5);
        Notification notification1 = notificationRepository.save(createNotification("1", sender, receiver, 0L, NotificationType.POST_LIKE));
        setMockingTime(4);
        Notification notification2 = notificationRepository.save(createNotification("2", sender, receiver, 0L, NotificationType.POST_COMMENT));
        setMockingTime(3);
        Notification notification3 = notificationRepository.save(createNotification("3", sender, receiver, 0L, NotificationType.POST_COMMENT_LIKE));
        setMockingTime(2);
        Notification notification4 = notificationRepository.save(createNotification("4", sender, receiver, 1L, NotificationType.OOTD_LIKE));
        setMockingTime(1);
        Notification notification5 = notificationRepository.save(createNotification("5", sender, receiver, 1L, NotificationType.OOTD_COMMENT));
        setMockingTime(0);
        Notification notification6 = notificationRepository.save(createNotification("6", sender, receiver, 1L, NotificationType.OOTD_COMMENT_LIKE));

        // when
        UserNotificationsDto userNotificationsDto = notificationService.getAllNotificationsByUserId(receiver.getId(), 0, 6);

        // then
        assertThat(userNotificationsDto.dataList()).hasSize(6)
                .extracting("title")
                .containsExactly(notification6.getTitle(), notification5.getTitle(), notification4.getTitle(),
                        notification3.getTitle(), notification2.getTitle(), notification1.getTitle());
    }

    public Notification createNotification(String content, User sender, User receiver, Long targetId, NotificationType notificationType) {
        return Notification.builder()
                .title(content + "title")
                .bodyMessage(content + "body message")
                .notificationSender(sender)
                .notificationReceiver(receiver)
                .targetId(targetId)
                .notificationType(notificationType)
                .build();
    }

    private void setMockingTime(int minute) {
        given(dateTimeProvider.getNow())
                .willReturn(Optional.of(
                        LocalDateTime.of(2025, 2, 1, 12, minute, 0)
                ));
    }

    private static User createTestUser(int number) {
        User user = User.builder()
                .email(number + "test@test.com")
                .gender(GenderType.MALE)
                .imageUrl(number + "test url")
                .nickname(number + "test nickname")
                .presentation(number + "test")
                .provider(EProvider.GOOGLE)
                .role(ERole.USER)
                .serialId(number + "123")
                .username(number + "test")
                .build();
        user.updateBirthDate("2000-05-02");
        return user;
    }
}