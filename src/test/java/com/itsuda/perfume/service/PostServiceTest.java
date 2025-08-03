package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Notification;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserFcmToken;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.post.CommentInfoDto;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.CreatedPostDto;
import com.itsuda.perfume.dto.response.post.PostCommentDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.NotificationRepository;
import com.itsuda.perfume.repository.PostRepository;
import com.itsuda.perfume.repository.UserFcmTokenRepository;
import com.itsuda.perfume.repository.UserLikeCommentRepository;
import com.itsuda.perfume.repository.UserLikePostRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class PostServiceTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @MockBean
    private FcmService fcmService;

    @SpyBean
    private AuditingHandler auditingHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserFcmTokenRepository userFcmTokenRepository;

    @Autowired
    private UserLikePostRepository userLikePostRepository;

    @Autowired
    private UserLikeCommentRepository userLikeCommentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createTestUser());
        userFcmTokenRepository.save(UserFcmToken.builder().user(user).fcmToken("testToken").build());
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @DisplayName("자유게시판에 올라온 게시글의 목록들을 최신순으로 조회한다.")
    @Test
    void getPostsOrderByNewestDescending() {
        // given
        setMockingTime(20);
        Post post1 = postRepository.save(createPost(1, user));

        setMockingTime(30);
        Post post2 = postRepository.save(createPost(2, user));

        setMockingTime(0);
        Post post3 = postRepository.save(createPost(3, user));

        // when
        PostMainDto result = postService.getPostsByOrderType(0, 3, PostOrderType.NEWEST_DESCENDING);

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("postId")
                .containsExactly(post2.getId(), post1.getId(), post3.getId());
    }

    @DisplayName("자유게시판에 올라온 게시글의 목록들을 역최신순으로 조회한다.")
    @Test
    void getPostsOrderByNewestAscending() {
        // given
        setMockingTime(20);
        Post post1 = postRepository.save(createPost(1, user));

        setMockingTime(30);
        Post post2 = postRepository.save(createPost(2, user));

        setMockingTime(0);
        Post post3 = postRepository.save(createPost(3, user));

        // when
        PostMainDto result = postService.getPostsByOrderType(0, 3, PostOrderType.NEWEST_ASCENDING);

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("postId")
                .containsExactly(post3.getId(), post1.getId(), post2.getId());
    }

    @DisplayName("자유게시판에 올라온 게시글의 목록들을 인기순으로 조회한다.")
    @Test
    void getPostsOrderByPopularDescending() {
        // given
        setMockingTime(20);
        Post post1 = postRepository.save(createPost(3, user));

        setMockingTime(30);
        Post post2 = postRepository.save(createPost(1, user));

        setMockingTime(0);
        Post post3 = postRepository.save(createPost(2, user));

        // when
        PostMainDto result = postService.getPostsByOrderType(0, 3, PostOrderType.POPULAR_DESCENDING);

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("postId")
                .containsExactly(post1.getId(), post3.getId(), post2.getId());
    }

    @DisplayName("자유게시판에 올라온 게시글의 목록들을 역인기순으로 조회한다.")
    @Test
    void getPostsOrderByPopularAscending() {
        // given
        setMockingTime(20);
        Post post1 = postRepository.save(createPost(3, user));

        setMockingTime(30);
        Post post2 = postRepository.save(createPost(1, user));

        setMockingTime(0);
        Post post3 = postRepository.save(createPost(2, user));

        // when
        PostMainDto result = postService.getPostsByOrderType(0, 3, PostOrderType.POPULAR_ASCENDING);

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("postId")
                .containsExactly(post2.getId(), post3.getId(), post1.getId());
    }

    @DisplayName("자유게시판에 제목, 내용을 가지는 게시글을 생성한다.")
    @Test
    void createPost() {
        // given
        String title = "test title";
        String content = "test content";
        List<String> tags = List.of();

        // when
        CreatedPostDto result = postService.createPost(user.getId(), title, content, tags);

        // then
        Optional<Post> post = postRepository.findById(result.postId());
        assertThat(post).isPresent();
        assertThat(post.get()).extracting("title", "content")
                .contains(title, content);
    }

    @DisplayName("자유게시판에 특정 태그를 가지는 게시글을 생성한다.")
    @Test
    void createPostTags() {
        // given
        String title = "test title";
        String content = "test content";
        List<String> tags = List.of("2025", "향수", "느좋");

        // when
        CreatedPostDto result = postService.createPost(user.getId(), title, content, tags);
        em.flush();
        em.clear();

        // then
        Post post = postRepository.findById(result.postId()).get();
        assertThat(post.getPostTags()).extracting(postTag -> postTag.getTag().getName())
                .contains("2025", "향수", "느좋");
    }

    @DisplayName("자유게시판에 올라온 게시글 ID에 해당하는 게시글의 작성 내용과 정보를 확인할 수 있다.")
    @Test
    void getDetailPostByPostId() {
        // given
        Post savedPost = postRepository.save(createPost(1, user));

        // when
        PostDetailDto result = postService.getPostDetailByPostId(savedPost.getId());

        // then
        assertThat(result.postInfo())
                .extracting("title", "content")
                .contains("test title1", "test content1");
    }

    @DisplayName("자유게시판에 올라온 게시글 ID에 해당하는 게시글의 작성자가 실제 작성한 작성자와 동일하다.")
    @Test
    void getDetailPostUserByPostId() {
        // given
        Post savedPost = postRepository.save(createPost(1, user));
        em.flush();
        em.clear();

        // when
        PostDetailDto result = postService.getPostDetailByPostId(savedPost.getId());

        // then
        assertThat(result.userInfo())
                .extracting("userId", "profileImageUrl", "nickname")
                .contains(user.getId(), user.getImageUrl(), user.getNickname());
    }

    @DisplayName("자유게시판에 올라온 게시글 ID에 해당하는 게시글이 없을 경우 게시물의 정보를 조회할 수 없다.")
    @Test
    void getDetailOfNotExistPost() {
        // given
        Post savedPost = postRepository.save(createPost(1, user));

        // when

        // then
        assertThatThrownBy(() -> postService.getPostDetailByPostId(savedPost.getId() + 1))
                .isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_FOUND_POST);
    }

    @DisplayName("자유게시글을 삭제하면 해당 자유게시글의 삭제날짜를 확인할 수 있다.")
    @Test
    void deletePostHasDeletedDate() {
        // given
        Post post = postRepository.save(createPost(1, user));

        // when
        postService.deletePostByPostId(post.getId(), user.getId());
        em.flush();
        em.clear();
        Post deletedPost = postRepository.findById(post.getId()).get();

        // then
        assertThat(deletedPost.getDeletedAt()).isNotNull();
    }

    @DisplayName("자유게시글의 작성자만 자유게시글을 삭제할 수 있다.")
    @Test
    void onlyOwnerCanDeletePost() {
        // given
        Post post = postRepository.save(createPost(1, user));
        User otherUser = userRepository.save(createTestUser(1));

        // when // then
        assertThatThrownBy(() -> postService.deletePostByPostId(post.getId(), otherUser.getId()))
                .isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.ONLY_POST_OWNER_DELETE);
    }

    @DisplayName("자유게시판의 게시글에 달린 댓글들을 모두 조회한다.")
    @Test
    void getAllCommentOfPost() {
        // given
        Post post = postRepository.save(createPost(1, user));
        Comment comment1 = commentRepository.save(createComment(1, null, post, user));
        Comment comment2 = commentRepository.save(createComment(2, null, post, user));
        commentRepository.save(createComment(3, comment1, post, user));
        commentRepository.save(createComment(4, comment1, post, user));
        commentRepository.save(createComment(5, comment2, post, user));

        em.flush();
        em.clear();

        // when
        CommentsDto result = postService.getCommentsByPostId(post.getId());

        // then
        assertThat(result.commentInfos()).extracting("content")
                .containsExactly(comment1.getContent(), comment2.getContent());
        assertThat(result.commentInfos()).extracting(commentInfo -> commentInfo.childCommentInfos().size())
                .containsExactly(2, 1);
    }

    @DisplayName("자유게시판에 달린 댓글들의 총 개수와 개별 대댓글의 개수가 조회된다.")
    @Test
    void getCommentCount() {
        // given
        Post post = postRepository.save(createPost(1, user));
        Comment comment1 = commentRepository.save(createComment(1, null, post, user));
        Comment comment2 = commentRepository.save(createComment(2, null, post, user));
        Comment comment1Child1 = commentRepository.save(createComment(3, comment1, post, user));
        Comment comment1Child2 = commentRepository.save(createComment(4, comment1, post, user));
        Comment comment2Child1 = commentRepository.save(createComment(5, comment2, post, user));

        em.flush();
        em.clear();

        // when
        CommentsDto result = postService.getCommentsByPostId(post.getId());

        // then
        assertThat(result.totalCommentCount()).isEqualTo(5);
        assertThat(result.commentInfos()).extracting(CommentInfoDto::commentCount)
                .containsExactly(2, 1);
    }

    @DisplayName("자유게시판 게시글에 좋아요를 요청하면 해당 게시글의 좋아요가 1만큼 오르고 사용자는 좋아요를 누른 것을 확인할 수 있다.")
    @Test
    void increasePostLikesAndCheckLike() {
        // given
        Post post = postRepository.save(createPost(0, user));
        int originLikeCount = post.getLikeCount();

        // when
        postService.sendLikeToPost(post.getId(), user.getId());

        // then
        assertThat(post.getLikeCount()).isEqualTo(originLikeCount + 1);
        assertThat(userLikePostRepository.existsByUserAndPost(user, post)).isTrue();
    }

    @DisplayName("사용자가 좋아요를 누른 자유게시판 게시글에 좋아요를 한번 더 누르면 좋아요가 취소된다.")
    @Test
    void cancelLikeToLikedPost() {
        // given
        Post post = postRepository.save(createPost(0, user));
        postService.sendLikeToPost(post.getId(), user.getId());
        int originLikeCount = post.getLikeCount();

        // when
        postService.sendLikeToPost(post.getId(), user.getId());

        // then
        assertThat(post.getLikeCount()).isEqualTo(originLikeCount - 1);
        assertThat(userLikePostRepository.existsByUserAndPost(user, post)).isFalse();
    }

    @DisplayName("자유게시글에 좋아요를 요청하면 자유게시글 작성자에게 좋아요 알림이 누적된다.")
    @Test
    void saveUserLikeNotificationToOotdWriter() {
        // given
        Post post = postRepository.save(createPost(0, user));
        doNothing().when(fcmService).sendFCMMessage(anyString(), anyString(), anyString());

        // when
        postService.sendLikeToPost(post.getId(), user.getId());

        // then
        assertThat(userLikePostRepository.existsByUserAndPost(user, post)).isTrue();
        assertThat(notificationRepository.findByNotificationReceiver(post.getUser())).hasSize(1);
    }

    @DisplayName("사용자가 게시글에 최상위 댓글을 단다.")
    @Test
    void writeCommentToPost() {
        // given
        Post post = postRepository.save(createPost(0, user));

        // when
        PostCommentDto result = postService.writeCommentToPost(post.getId(), user.getId(), null, "test comment");
        Optional<Comment> comment = commentRepository.findById(result.commentId());

        // then
        assertThat(comment.isPresent()).isTrue();
        assertThat(comment.get()).extracting("parentComment", "content")
                .contains(null, "test comment");
    }

    @DisplayName("사용자가 게시글에 달린 댓글의 답글을 단다.")
    @Test
    void writeReplyToPostComment() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createComment(1, null, post, user));

        // when
        PostCommentDto result = postService.writeCommentToPost(post.getId(), user.getId(),
                comment.getId(), "test comment");
        Optional<Comment> reply = commentRepository.findById(result.commentId());

        // then
        assertThat(reply.isPresent()).isTrue();
        assertThat(reply.get()).extracting("parentComment", "content")
                .contains(comment, "test comment");
    }

    @DisplayName("사용자가 자유게시글에 댓글을 달면, 자우게시글 작성자에게 알림이 누적된다.")
    @Test
    void savePostCommentNotificationToPostWriter() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createComment(1, null, post, user));
        doNothing().when(fcmService).sendFCMMessage(anyString(), anyString(), anyString());

        // when
        PostCommentDto result = postService.writeCommentToPost(post.getId(), user.getId(),
                comment.getId(), "test comment");
        List<Notification> notifications = notificationRepository.findByNotificationReceiver(user);

        // then
        assertThat(notifications).hasSize(1);
        assertThat(notifications).extracting("commentWriter").containsExactly(user);
    }

    @DisplayName("댓글을 삭제하면 해당 댓글의 삭제날짜를 확인할 수 있고 메시지가 삭제된 메시지입니다라고 바뀌며 좋아요는 0이 된다.")
    @Test
    void deletedCommentHasDeletedDateAndMessageAndLikeCountIsChanged() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createComment(0, null, post, user));

        // when
        postService.deletePostComment(user.getId(), comment.getId());
        em.flush();
        em.clear();
        Comment deletedComment = commentRepository.findById(comment.getId()).get();

        // then
        assertThat(deletedComment.getDeletedAt()).isNotNull();
        assertThat(deletedComment).extracting("content", "likeCount")
                .contains("삭제된 댓글입니다", 0);
    }

    @DisplayName("댓글의 작성자만 댓글을 삭제할 수 있다.")
    @Test
    void onlyOwnerCanDeleteComment() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createComment(0, null, post, user));
        User otherUser = userRepository.save(createTestUser(1));

        // when // then
        assertThatThrownBy(() -> postService.deletePostComment(otherUser.getId(), comment.getId()))
                .isInstanceOf(RestApiException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.ONLY_COMMENT_OWNER_DELETE);
    }

    @DisplayName("댓글에 좋아요를 요청하면 좋아요가 1만큼 오르고 사용자는 댓글에 좋아요를 누른 것을 확인할 수 있다.")
    @Test
    void increasePostCommentLikesAndCheckLike() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createComment(0, null, post, user));
        int originLikeCount = comment.getLikeCount();

        // when
        postService.sendLikeToPostComment(user.getId(), comment.getId());

        // then
        assertThat(comment.getLikeCount()).isEqualTo(originLikeCount + 1);
        assertThat(userLikeCommentRepository.existsByUserAndComment(user, comment)).isTrue();
    }

    @DisplayName("사용자가 좋아요를 누른 댓글에 좋아요를 한번 더 누르면 좋아요가 취소된다.")
    @Test
    void cancelLikeToLikedOotdComment() {
        // given
        Post post = postRepository.save(createPost(0, user));
        Comment comment = commentRepository.save(createComment(0, null, post, user));
        postService.sendLikeToPostComment(user.getId(), comment.getId());
        int originLikeCount = comment.getLikeCount();

        // when
        postService.sendLikeToPostComment(user.getId(), comment.getId());

        // then
        assertThat(comment.getLikeCount()).isEqualTo(originLikeCount - 1);
        assertThat(userLikeCommentRepository.existsByUserAndComment(user, comment)).isFalse();
    }

    private void setMockingTime(int minute) {
        given(dateTimeProvider.getNow())
                .willReturn(Optional.of(
                        LocalDateTime.of(2025, 2, 1, 12, minute, 0)
                ));
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

    private static Post createPost(int number, User user) {
        return Post.builder()
                .title("test title" + number)
                .content("test content" + number)
                .likeCount(number)
                .user(user)
                .build();
    }

    private static Comment createComment(int number, Comment parent, Post post, User user) {
        return Comment.builder()
                .content("test content" + number)
                .likeCount(number)
                .parentComment(parent)
                .post(post)
                .user(user)
                .build();
    }
}