package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.Report;
import com.itsuda.perfume.domain.Review;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PotentialType;
import com.itsuda.perfume.domain.type.ReportTargetType;
import com.itsuda.perfume.domain.type.ReportType;
import com.itsuda.perfume.dto.request.report.CommentReportDto;
import com.itsuda.perfume.dto.request.report.OotdReportDto;
import com.itsuda.perfume.dto.request.report.PostReportDto;
import com.itsuda.perfume.dto.request.report.ReviewReportDto;
import com.itsuda.perfume.dto.response.report.ReportedCommentDto;
import com.itsuda.perfume.dto.response.report.ReportedOotdDto;
import com.itsuda.perfume.dto.response.report.ReportedPostDto;
import com.itsuda.perfume.dto.response.report.ReportedReviewDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.PostRepository;
import com.itsuda.perfume.repository.ReportRepository;
import com.itsuda.perfume.repository.ReviewRepository;
import com.itsuda.perfume.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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

    @DisplayName("신고자, 자유게시글 ID, 신고 사유를 통해 자유게시글을 신고할 수 있다.")
    @Test
    void reportPostByReporterAndPostIdAndReportType() {
        // given
        Post post = postRepository.save(createPost(0, user));

        // when
        ReportedPostDto reportedPostDto = reportService.reportPostByPostIdAndUserId(new PostReportDto(ReportType.SPAM_AD, null),
                post.getId(), user.getId());

        // then
        assertThat(reportRepository.existsById(reportedPostDto.reportId())).isTrue();
    }

    @DisplayName("존재하지 않는 자유게시글은 신고할 수 없다.")
    @Test
    void cannotReportNotExistPost() {
        // given
        Post post = createPost(0, user);

        // when // then
        assertThatThrownBy(() -> reportService.reportPostByPostIdAndUserId(new PostReportDto(ReportType.SPAM_AD, null),
                Optional.ofNullable(post.getId()).orElse(0L), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(NOT_FOUND_POST);
    }

    @DisplayName("신고자가 이미 신고한 자유게시글은 다시 신고할 수 없다.")
    @Test
    void cannotReportAlreadyReportedPost() {
        // given
        Post post = postRepository.save(createPost(0, user));
        reportRepository.save(createReport(user, ReportType.SPAM_AD, ReportTargetType.POST, post.getId(), null));

        // when // then
        assertThatThrownBy(() -> reportService.reportPostByPostIdAndUserId(new PostReportDto(ReportType.SPAM_AD, null),
                post.getId(), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(ALREADY_REPORTED_POST);
    }

    @DisplayName("신고자, 댓글 ID, 신고 사유를 통해 댓글을 신고할 수 있다.")
    @Test
    void reportCommentByReporterAndCommentIdAndReportType() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createPostComment(0, null, post, user));

        // when
        ReportedCommentDto reportedCommentDto = reportService.reportCommentByCommentId(new CommentReportDto(ReportType.SPAM_AD, null),
                comment.getId(), user.getId());

        // then
        assertThat(reportRepository.existsById(reportedCommentDto.reportId())).isTrue();
    }

    @DisplayName("존재하지 않는 댓글은 신고할 수 없다.")
    @Test
    void cannotReportNotExistComment() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = createPostComment(0, null, post, user);

        // when // then
        assertThatThrownBy(() -> reportService.reportCommentByCommentId(new CommentReportDto(ReportType.SPAM_AD, null),
                Optional.ofNullable(comment.getId()).orElse(0L), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(NOT_FOUND_COMMENT);
    }

    @DisplayName("신고자가 이미 신고한 댓글은 다시 신고할 수 없다.")
    @Test
    void cannotReportAlreadyReportedComment() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createPostComment(0, null, post, user));
        reportRepository.save(createReport(user, ReportType.SPAM_AD, ReportTargetType.COMMENT, comment.getId(), null));

        // when // then
        assertThatThrownBy(() -> reportService.reportCommentByCommentId(new CommentReportDto(ReportType.SPAM_AD, null),
                comment.getId(), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(ALREADY_REPORTED_COMMENT);
    }

    @DisplayName("신고자, 리뷰 ID, 신고 사유를 통해 리뷰를 신고할 수 있다.")
    @Test
    void reportReviewByReporterAndReviewIdAndReportType() {
        // given
        Perfume perfume = perfumeRepository.save(createPerfume("test"));
        Review review = reviewRepository.save(createReview(0, user, perfume));

        // when
        ReportedReviewDto reportedReviewDto = reportService.reportReviewByReviewId(new ReviewReportDto(ReportType.SPAM_AD, null),
                review.getId(), user.getId());

        // then
        assertThat(reportRepository.existsById(reportedReviewDto.reportId())).isTrue();
    }

    @DisplayName("존재하지 않는 리뷰는 신고할 수 없다.")
    @Test
    void cannotReportNotExistReview() {
        // given
        Perfume perfume = perfumeRepository.save(createPerfume("test"));
        Review review = createReview(0, user, perfume);

        // when // then
        assertThatThrownBy(() -> reportService.reportReviewByReviewId(new ReviewReportDto(ReportType.SPAM_AD, null),
                Optional.ofNullable(review.getId()).orElse(0L), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(NOT_FOUND_REVIEW);
    }

    @DisplayName("신고자가 이미 신고한 리뷰는 다시 신고할 수 없다.")
    @Test
    void cannotReportAlreadyReportedReview() {
        // given
        Perfume perfume = perfumeRepository.save(createPerfume("test"));
        Review review = reviewRepository.save(createReview(0, user, perfume));
        reportRepository.save(createReport(user, ReportType.SPAM_AD, ReportTargetType.REVIEW, review.getId(), null));

        // when // then
        assertThatThrownBy(() -> reportService.reportReviewByReviewId(new ReviewReportDto(ReportType.SPAM_AD, null),
                review.getId(), user.getId())).isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(ALREADY_REPORTED_REVIEW);
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
                .volume(10 * number)
                .content("test" + number)
                .user(user)
                .build();
    }

    private static Post createPost(int number, User user) {
        return Post.builder()
                .title("test title" + number)
                .content("test content" + number)
                .user(user)
                .build();
    }

    private static Comment createPostComment(int number, Comment parent, Post post, User user) {
        return Comment.builder()
                .content("test content" + number)
                .parentComment(parent)
                .post(post)
                .user(user)
                .build();
    }

    private static Perfume createPerfume(String name) {
        return Perfume.builder()
                .name(name + " perfume")
                .imageUri(name + " url")
                .gender(GenderType.MALE)
                .brand(BrandType.CHANEL)
                .country(CountryType.FRANCE)
                .potential(PotentialType.EDT)
                .description(name + " desc")
                .registeredAt(LocalDate.of(2025, 2, 1))
                .build();
    }

    private static Review createReview(int number, User user, Perfume perfume) {
        return Review.builder()
                .content("test content" + number)
                .score(number)
                .createdAt(LocalDateTime.now())
                .modifiedAt(null)
                .perfumeGender(GenderType.MALE)
                .potentialScore(number)
                .weight(number)
                .user(user)
                .perfume(perfume).build();
    }
}