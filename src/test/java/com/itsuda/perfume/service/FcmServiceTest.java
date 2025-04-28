package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserFcmToken;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.repository.UserFcmTokenRepository;
import com.itsuda.perfume.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class FcmServiceTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

    @Autowired
    private EntityManager em;

    private User user;
    @Autowired
    private UserFcmTokenRepository userFcmTokenRepository;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createTestUser());
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @DisplayName("사용자의 FCM 토큰을 저장한다.")
    @Test
    void getFcmToken() {
        // given
        String fcmToken = "testtoken123";

        // when
        fcmService.saveUserFcmToken(user.getId(), fcmToken);
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(user);

        // then
        assertThat(userFcmToken).isPresent()
                .get().extracting("fcmToken").isEqualTo(fcmToken);
    }

    private static User createTestUser() {
        User user = User.builder()
                .email("test@test.com")
                .gender(GenderType.MALE)
                .imageUrl("test url")
                .nickname("test nickname")
                .presentation("test")
                .provider(EProvider.GOOGLE)
                .role(ERole.USER)
                .serialId("123")
                .username("test")
                .build();
        user.updateBirthDate("2000-05-02");
        return user;
    }
}