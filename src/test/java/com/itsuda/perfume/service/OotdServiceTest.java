package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.domain.type.PotentialType;
import com.itsuda.perfume.dto.response.ootd.CommentInfoDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.CreatedOotdDto;
import com.itsuda.perfume.dto.response.ootd.OotdCommentDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.OotdImageRepository;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.UserLikeOotdRepository;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class OotdServiceTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    @Autowired
    OotdService ootdService;

    @Autowired
    OotdImageRepository ootdImageRepository;

    @Autowired
    OotdRepository ootdRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLikeOotdRepository userLikeOotdRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    private Perfume perfume;

    private User user;

    @BeforeEach
    void setUp() {
        perfume = createTestPerfume();
        perfumeRepository.save(perfume);
        user = userRepository.save(createTestUser());
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @DisplayName("OOTD 게시물들의 썸네일의 정보를 최신순으로 조회한다.")
    @Test
    void getOotdThumbnailsSortedByNewestDescending() {
        // given
        setMockingTime(20);
        Ootd savedOotd1 = ootdRepository.save(createOotd(1));
        ootdImageRepository.save(createOotdImage(0, savedOotd1));

        setMockingTime(30);
        Ootd savedOotd2 = ootdRepository.save(createOotd(2));
        ootdImageRepository.save(createOotdImage(0, savedOotd2));

        setMockingTime(0);
        Ootd savedOotd3 = ootdRepository.save(createOotd(3));
        ootdImageRepository.save(createOotdImage(0, savedOotd3));

        // when
        OotdMainDto result = ootdService.getOotdThumbnailsByOrderType(0, 3, OotdOrderType.NEWEST_DESCENDING, user.getId());

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("ootdId")
                .containsExactly(savedOotd2.getId(), savedOotd1.getId(), savedOotd3.getId());
    }

    @DisplayName("OOTD 게시물들의 썸네일의 정보를 역최신순으로 조회한다.")
    @Test
    void getOotdThumbnailsSortedByNewestAscending() {
        // given
        setMockingTime(20);
        Ootd savedOotd1 = ootdRepository.save(createOotd(1));
        ootdImageRepository.save(createOotdImage(0, savedOotd1));

        setMockingTime(30);
        Ootd savedOotd2 = ootdRepository.save(createOotd(2));
        ootdImageRepository.save(createOotdImage(0, savedOotd2));

        setMockingTime(0);
        Ootd savedOotd3 = ootdRepository.save(createOotd(3));
        ootdImageRepository.save(createOotdImage(0, savedOotd3));

        // when
        OotdMainDto result = ootdService.getOotdThumbnailsByOrderType(0, 3, OotdOrderType.NEWEST_ASCENDING, user.getId());

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("ootdId")
                .containsExactly(savedOotd3.getId(), savedOotd1.getId(), savedOotd2.getId());
    }

    @DisplayName("OOTD 게시물들의 썸네일의 정보를 인기순으로 조회한다.")
    @Test
    void getOotdThumbnailsSortedByPopularDescending() {
        // given
        Ootd savedOotd1 = ootdRepository.save(createOotd(3));
        ootdImageRepository.save(createOotdImage(0, savedOotd1));

        Ootd savedOotd2 = ootdRepository.save(createOotd(1));
        ootdImageRepository.save(createOotdImage(0, savedOotd2));

        Ootd savedOotd3 = ootdRepository.save(createOotd(2));
        ootdImageRepository.save(createOotdImage(0, savedOotd3));

        // when
        OotdMainDto result = ootdService.getOotdThumbnailsByOrderType(0, 3, OotdOrderType.POPULAR_DESCENDING, user.getId());

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("ootdId")
                .containsExactly(savedOotd1.getId(), savedOotd3.getId(), savedOotd2.getId());
    }

    @DisplayName("OOTD 게시물들의 썸네일의 정보를 역인기순으로 조회한다.")
    @Test
    void getOotdThumbnailsSortedByPopularAscending() {
        // given
        Ootd savedOotd1 = ootdRepository.save(createOotd(3));
        ootdImageRepository.save(createOotdImage(0, savedOotd1));

        Ootd savedOotd2 = ootdRepository.save(createOotd(1));
        ootdImageRepository.save(createOotdImage(0, savedOotd2));

        Ootd savedOotd3 = ootdRepository.save(createOotd(2));
        ootdImageRepository.save(createOotdImage(0, savedOotd3));

        // when
        OotdMainDto result = ootdService.getOotdThumbnailsByOrderType(0, 3, OotdOrderType.POPULAR_ASCENDING, user.getId());

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("ootdId")
                .containsExactly(savedOotd2.getId(), savedOotd3.getId(), savedOotd1.getId());
    }

    @DisplayName("OOTD에 이미지와 태그, 내용, 향수 정보를 가지는 게시글을 생성한다.")
    @Test
    void createPost() {
        // given
        String content = "test content";
        List<String> tags = List.of();
        List<MultipartFile> mockMultipartFiles = List.of(new MockMultipartFile("test file1", "test1.png", MediaType.IMAGE_JPEG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test file2", "test2.png", MediaType.IMAGE_JPEG_VALUE, "test2".getBytes()),
                new MockMultipartFile("test file3", "test3.png", MediaType.IMAGE_JPEG_VALUE, "test3".getBytes()));

        // when
        CreatedOotdDto result = ootdService.createOotd(user.getId(), content, tags, 10, perfume.getId(), mockMultipartFiles);

        // then
        Optional<Ootd> ootd = ootdRepository.findById(result.ootdId());
        assertThat(ootd).isPresent();
        assertThat(ootd.get()).extracting("content", "perfume")
                .contains(content, perfume);
    }

    @DisplayName("OOTD에 특정 태그를 가지는 게시글을 생성한다.")
    @Test
    void createPostTags() {
        // given
        String content = "test content";
        List<String> tags = List.of("2025", "향수", "느좋");
        List<MultipartFile> mockMultipartFiles = List.of(new MockMultipartFile("test file1", "test1.png", MediaType.IMAGE_JPEG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test file2", "test2.png", MediaType.IMAGE_JPEG_VALUE, "test2".getBytes()),
                new MockMultipartFile("test file3", "test3.png", MediaType.IMAGE_JPEG_VALUE, "test3".getBytes()));

        // when
        CreatedOotdDto result = ootdService.createOotd(user.getId(), content, tags, 10, perfume.getId(), mockMultipartFiles);
        em.flush();
        em.clear();

        // then
        Ootd ootd = ootdRepository.findById(result.ootdId()).get();
        assertThat(ootd.getOotdTags()).extracting(ootdTag -> ootdTag.getTag().getName())
                .contains("2025", "향수", "느좋");
    }

    @DisplayName("OOTD 게시글 아이디에 해당하는 OOTD 게시글의 정보와 이미지들을 조회한다.")
    @Test
    void getOotdPostAndImages() {
        // given
        Ootd savedOotd = ootdRepository.save(createOotd(1));
        ootdImageRepository.saveAll(List.of(createOotdImage(0, savedOotd),
                createOotdImage(1, savedOotd),
                createOotdImage(2, savedOotd)));
        em.flush();
        em.clear();

        // when
        OotdDetailDto ootdDetail = ootdService.getOotdDetailByOotdId(savedOotd.getId(), user.getId());

        // then
        assertThat(ootdDetail).extracting("ootdInfo.ootdId", "ootdInfo.createdAt")
                .contains(savedOotd.getId(), savedOotd.getCreatedAt());
        assertThat(ootdDetail.ootdInfo().ootdImageUrls()).hasSize(3);
    }

    @DisplayName("OOTD 게시글에 달린 댓글들을 모두 조회한다.")
    @Test
    void getAllCommentOfOotd() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(1));
        Comment comment1 = commentRepository.save(createComment(1, null, ootd, user));
        Comment comment2 = commentRepository.save(createComment(2, null, ootd, user));
        commentRepository.save(createComment(3, comment1, ootd, user));
        commentRepository.save(createComment(4, comment1, ootd, user));
        commentRepository.save(createComment(5, comment2, ootd, user));

        em.flush();
        em.clear();

        // when
        CommentsDto result = ootdService.getCommentsByOotdId(ootd.getId());

        // then
        assertThat(result.commentInfos()).extracting("content")
                .containsExactly(comment1.getContent(), comment2.getContent());
        assertThat(result.commentInfos()).extracting(commentInfo -> commentInfo.childCommentInfos().size())
                .containsExactly(2, 1);
    }

    @DisplayName("OOTD에 달린 댓글들의 총 개수와 개별 대댓글의 개수가 조회된다.")
    @Test
    void getCommentCount() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(1));
        Comment comment1 = commentRepository.save(createComment(1, null, ootd, user));
        Comment comment2 = commentRepository.save(createComment(2, null, ootd, user));
        Comment comment1Child1 = commentRepository.save(createComment(3, comment1, ootd, user));
        Comment comment1Child2 = commentRepository.save(createComment(4, comment1, ootd, user));
        Comment comment2Child1 = commentRepository.save(createComment(5, comment2, ootd, user));

        em.flush();
        em.clear();

        // when
        CommentsDto result = ootdService.getCommentsByOotdId(ootd.getId());

        // then
        assertThat(result.totalCommentCount()).isEqualTo(5);
        assertThat(result.commentInfos()).extracting(CommentInfoDto::commentCount)
                .containsExactly(2, 1);
    }

    @DisplayName("OOTD 게시글에 좋아요를 요청하면 해당 게시글의 좋아요가 1만큼 오르고 사용자는 좋아요를 누른 것을 확인할 수 있다.")
    @Test
    void increaseOotdLikesAndCheckLike() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0));
        ootdImageRepository.save(createOotdImage(0, ootd));
        int originLikeCount = ootd.getLikeCount();

        // when
        ootdService.sendLikeToOotd(ootd.getId(), user.getId());

        // then
        assertThat(ootd.getLikeCount()).isEqualTo(originLikeCount + 1);
        assertThat(userLikeOotdRepository.existsByUserAndOotd(user, ootd)).isTrue();
    }

    @DisplayName("사용자가 좋아요를 누른 OOTD 게시글에 좋아요를 한번 더 누르면 좋아요가 취소된다.")
    @Test
    void cancelLikeToLikedOotd() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0));
        ootdImageRepository.save(createOotdImage(0, ootd));
        ootdService.sendLikeToOotd(ootd.getId(), user.getId());
        int originLikeCount = ootd.getLikeCount();

        // when
        ootdService.sendLikeToOotd(ootd.getId(), user.getId());

        // then
        assertThat(ootd.getLikeCount()).isEqualTo(originLikeCount - 1);
        assertThat(userLikeOotdRepository.existsByUserAndOotd(user, ootd)).isFalse();
    }

    @DisplayName("사용자가 게시글에 최상위 댓글을 단다.")
    @Test
    void writeCommentToOotd() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0));

        // when
        OotdCommentDto result = ootdService.writeCommentToOotd(ootd.getId(), user.getId(), null, "test comment");
        Optional<Comment> comment = commentRepository.findById(result.commentId());

        // then
        assertThat(comment.isPresent()).isTrue();
        assertThat(comment.get()).extracting("parentComment", "content")
                .contains(null, "test comment");
    }

    @DisplayName("사용자가 게시글에 달린 댓글의 답글을 단다.")
    @Test
    void writeReplyToOotdComment() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0));
        Comment comment = commentRepository.save(createComment(1, null, ootd, user));

        // when
        OotdCommentDto result = ootdService.writeCommentToOotd(ootd.getId(), user.getId(),
                comment.getId(), "test comment");
        Optional<Comment> reply = commentRepository.findById(result.commentId());

        // then
        assertThat(reply.isPresent()).isTrue();
        assertThat(reply.get()).extracting("parentComment", "content")
                .contains(comment, "test comment");
    }

    private void setMockingTime(int minute) {
        given(dateTimeProvider.getNow())
                .willReturn(Optional.of(
                        LocalDateTime.of(2025, 2, 1, 12, minute, 0)
                ));
    }

    private Ootd createOotd(int number) {
        return Ootd.builder()
                .likeCount(number)
                .volume(10 * number)
                .content("test" + number)
                .perfume(perfume)
                .user(user)
                .build();
    }

    private static OotdImage createOotdImage(int sequence, Ootd ootd) {
        return OotdImage.builder()
                .originName("test" + sequence)
                .saveName("test" + sequence)
                .sequence(sequence)
                .ootd(ootd)
                .build();
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

    private static Perfume createTestPerfume() {
        return Perfume.builder()
                .name("test perfume")
                .imageUri("test url")
                .gender(GenderType.MALE)
                .brand(BrandType.CHANEL)
                .country(CountryType.FRANCE)
                .potential(PotentialType.EDT)
                .description("test desc")
                .registeredAt(LocalDate.of(2025, 2, 1))
                .build();
    }

    private static Comment createComment(int number, Comment parent, Ootd ootd, User user) {
        return Comment.builder()
                .content("test content" + number)
                .likeCount(number)
                .parentComment(parent)
                .ootd(ootd)
                .user(user)
                .build();
    }
}