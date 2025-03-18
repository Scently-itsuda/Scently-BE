package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.PostRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class PostServiceTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createTestUser());
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @DisplayName("자유게시판에 올라온 게시글의 목록들을 최신순으로 조회한다.")
    @Test
    void getPostsOrderByNewest() {
        // given
        setMockingTime(20);
        Post post1 = postRepository.save(createPost(1, user));

        setMockingTime(30);
        Post post2 = postRepository.save(createPost(2, user));

        setMockingTime(0);
        Post post3 = postRepository.save(createPost(3, user));

        // when
        PostMainDto result = postService.getPostsByOrderType(0, 3, PostOrderType.NEWEST);

        // then
        assertThat(result.dataList())
                .extracting("title", "content")
                .containsExactly(
                        tuple("test title2", "test content2"),
                        tuple("test title1", "test content1"),
                        tuple("test title3", "test content3")
                );
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
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_FOUNT_POST);
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

    private static Post createPost(int number, User user) {
        return Post.builder()
                .title("test title" + number)
                .content("test content" + number)
                .user(user)
                .build();
    }
}