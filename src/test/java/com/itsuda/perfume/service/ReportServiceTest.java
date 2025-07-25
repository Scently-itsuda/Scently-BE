package com.itsuda.perfume.service;

import com.google.firebase.database.core.Repo;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.Report;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.ReportTargetType;
import com.itsuda.perfume.domain.type.ReportType;
import com.itsuda.perfume.dto.request.report.OotdReportDto;
import com.itsuda.perfume.dto.response.report.ReportedOotdDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.ReportRepository;
import com.itsuda.perfume.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.itsuda.perfume.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private ReportRepository reportRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createTestUser(0));
    }

    @DisplayName("신고자, OOTD ID, 신고 사유를 통해 OOTD 게시글을 신고할 수 있다.")
    @Test
    void reportOotdByReporterAndOotdIdAndReportType() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0, user));

        // when
        ReportedOotdDto reportedOotdDto = reportService.reportOotdByOotdIdAndUserId(new OotdReportDto(ReportType.SPAM_AD, null),
                ootd.getId(), user.getId());

        // then
        assertThat(reportRepository.existsById(reportedOotdDto.reportId())).isTrue();
    }

    @DisplayName("존재하지 않는 OOTD는 신고할 수 없다.")
    @Test
    void cannotReportNotExistOotd() {
        // given
        Ootd ootd = createOotd(0, user);

        // when // then
        assertThatThrownBy(() -> reportService.reportOotdByOotdIdAndUserId(new OotdReportDto(ReportType.SPAM_AD, null),
                Optional.ofNullable(ootd.getId()).orElse(0L), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(NOT_FOUND_OOTD);
    }

    @DisplayName("신고자가 이미 신고한 OOTD는 다시 신고할 수 없다.")
    @Test
    void cannotReportAlreadyReportedOotd() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0, user));
        reportRepository.save(createReport(user, ReportType.SPAM_AD, ReportTargetType.OOTD, ootd.getId(), null));

        // when // then
        assertThatThrownBy(() -> reportService.reportOotdByOotdIdAndUserId(new OotdReportDto(ReportType.SPAM_AD, null),
                ootd.getId(), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(ALREADY_REPORTED_OOTD);
    }

    private static Report createReport(User user, ReportType reportType, ReportTargetType reportTargetType, Long targetId, String otherReason) {
        return Report.builder()
                .reporter(user)
                .reportType(reportType)
                .reportTargetType(reportTargetType)
                .targetId(targetId)
                .otherReason(otherReason)
                .build();
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

    private Ootd createOotd(int number, User user) {
        return Ootd.builder()
                .likeCount(number)
                .volume(10 * number)
                .content("test" + number)
                .user(user)
                .build();
    }
}