package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private User user;

    private Post post;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createTestUser());
        post = postRepository.save(createPost(0, user));
    }

    @DisplayName("특정 포스트의 댓글들을 조회할 시, 최상위 댓글들을 조회한다.")
    @Test
    void test() {
        // given
        Comment comment1 = commentRepository.save(createComment(1, null, post, user));
        Comment comment2 = commentRepository.save(createComment(2, null, post, user));
        commentRepository.save(createComment(3, comment1, post, user));
        commentRepository.save(createComment(4, comment1, post, user));
        commentRepository.save(createComment(5, comment2, post, user));

        em.flush();
        em.clear();

        // when
        List<Comment> result = commentRepository.findAllByPostAndParentCommentIsNull(post);

        // then
        assertThat(result).extracting("content")
                .containsExactly("test content1", "test content2");
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

    private static Post createPost(int number, User user) {
        return Post.builder()
                .title("test title" + number)
                .content("test content" + number)
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