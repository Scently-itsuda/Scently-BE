package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.post.PostMainDto;
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
    void test() {
        // given
        setMockingTime(20);
        Post post1 = postRepository.save(Post.builder().title("test title1").content("test1").user(user).build());

        setMockingTime(30);
        Post post2 = postRepository.save(Post.builder().title("test title2").content("test2").user(user).build());

        setMockingTime(0);
        Post post3 = postRepository.save(Post.builder().title("test title3").content("test3").user(user).build());

        // when
        PostMainDto result = postService.getPostsByOrderType(0, 3, PostOrderType.NEWEST);

        // then
        assertThat(result.dataList())
                .extracting("title", "content")
                .containsExactly(
                        tuple("test title2", "test2"),
                        tuple("test title1", "test1"),
                        tuple("test title3", "test3")
                );
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
}