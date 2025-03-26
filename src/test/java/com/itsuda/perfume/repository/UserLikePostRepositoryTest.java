package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikePost;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UserLikePostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserLikePostRepository userLikePostRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createTestUser());
    }

    @DisplayName("사용자가 OOTD 게시글에 좋아요를 눌렀는지 확인한다.")
    @Test
    void userLikeOotd() {
        // given
        Post post = postRepository.save(createPost(0, user));
        userLikePostRepository.save(UserLikePost.builder().user(user).post(post).build());

        // when
        Boolean isUserLikedPost = userLikePostRepository.existsByUserAndPost(user, post);

        // then
        assertThat(isUserLikedPost).isTrue();
    }

    @DisplayName("사용자가 OOTD 게시글에 좋아요를 누르지 않았는지 확인한다.")
    @Test
    void userNotLikedOotd() {
        // given
        Post post = postRepository.save(createPost(0, user));

        // when
        Boolean isUserLikedPost = userLikePostRepository.existsByUserAndPost(user, post);

        // then
        assertThat(isUserLikedPost).isFalse();
    }

    private static Post createPost(int number, User user) {
        return Post.builder()
                .title("test title" + number)
                .content("test content" + number)
                .user(user)
                .build();
    }

    private static User createTestUser() {
        return User.builder()
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
    }
}