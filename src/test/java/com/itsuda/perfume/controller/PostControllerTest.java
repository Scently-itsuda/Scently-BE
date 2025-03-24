package com.itsuda.perfume.controller;

import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.ootd.OotdLikeDto;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostLikeDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@WithMockUser
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @DisplayName("자유게시판의 게시글 목록들을 조회한다.")
    @Test
    void getPosts() throws Exception {
        // given
        PostMainDto result = new PostMainDto(null, null);
        Mockito.when(postService.getPostsByOrderType(anyInt(), anyInt(), any(PostOrderType.class)))
                .thenReturn(result);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/posts")
                                .queryParam("page", "0")
                                .queryParam("size", "3")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));

    }

    @DisplayName("자유게시판의 게시글 ID를 기반으로 게시글을 상세 조회한다.")
    @Test
    void getPostDetail() throws Exception {
        // given
        PostDetailDto result = new PostDetailDto(null, null);
        Mockito.when(postService.getPostDetailByPostId(anyLong())).thenReturn(result);

        // when  // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));

    }

    @DisplayName("자유게시판의 게시글 ID에 달린 댓글을 조회한다.")
    @Test
    void getComments() throws Exception {
        // given
        CommentsDto result = new CommentsDto(null, 0);
        Mockito.when(postService.getCommentsByPostId(anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("자유게시판 게시글에 좋아요를 눌러서 좋아요를 요청한다.")
    @Test
    void test() throws Exception {
        // given
        PostLikeDto result = new PostLikeDto(null, null);

        Mockito.when(postService.sendLikeToPost(anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }
}