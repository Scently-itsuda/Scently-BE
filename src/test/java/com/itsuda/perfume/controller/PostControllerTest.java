package com.itsuda.perfume.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.request.post.CreatePostDto;
import com.itsuda.perfume.dto.request.post.PostCommentRequestDto;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

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

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("자유게시판의 게시글 목록들을 조회한다.")
    @Test
    void getPosts() throws Exception {
        // given
        PostMainDto result = new PostMainDto(null, null);
        Mockito.when(postService.getPostsByOrderType(anyInt(), anyInt(), "", any(PostOrderType.class)))
                .thenReturn(result);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/posts")
                                .queryParam("page", "0")
                                .queryParam("size", "3")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));

    }

    @DisplayName("자유게시판 게시글을 작성할 때는 제목이 비어있거나 공백이면 안된다.")
    @Test
    void postMustHaveTitle() throws Exception {
        // given
        CreatePostDto request = new CreatePostDto(" ", "test content", List.of("태그1", "태그2"));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("자유게시글의 제목은 공백이 아닌 글자가 있어야합니다"));
    }

    @DisplayName("자유게시판 게시글을 작성할 때는 내용이 비어있거나 공백이면 안된다.")
    @Test
    void postMustHaveContent() throws Exception {
        // given
        CreatePostDto request = new CreatePostDto("test title", " ", List.of("태그1", "태그2"));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("자유게시글의 내용은 공백이 아닌 글자가 있어야합니다"));
    }

    @DisplayName("태그는 10개까지만 달 수 있다.")
    @Test
    void tagSizeIsSmallerThan10() throws Exception {
        // given
        CreatePostDto request = new CreatePostDto("test title", " ",
                List.of("태그1", "태그2", "태그3", "태그4", "태그5", "태그6", "태그7", "태그8", "태그9", "태그10", "태그11"));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("태그는 최대 10개까지 가능합니다"));
    }

    @DisplayName("각 태그의 내용은 공백이 아니며 15자 이하이다.")
    @ValueSource(strings = {"", " ", "이태그는총열다섯글자를넘습니다.", "공벡포함 태그"})
    @ParameterizedTest
    void tagIsNotBlankAndSmallerThan15(String tag) throws Exception {
        // given
        CreatePostDto request = new CreatePostDto("test title", "test content",
                List.of(tag));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("태그는 공백이 아닌 1~15자여야 하며 공백문자를 포함하면 안됩니다"));
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

    @DisplayName("자유게시판에 게시글 ID에 맞는 게시글을 삭제한다.")
    @Test
    void deletePostByPostId() throws Exception {
        // given
        Mockito.doNothing().when(postService).deletePostByPostId(anyLong(), anyLong());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/1").with(csrf()))
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
    void likePost() throws Exception {
        // given
        Mockito.doNothing().when(postService).sendLikeToPost(anyLong(), anyLong());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("자유게시판 게시글에 내용이 없는(공백으로 찬) 댓글은 달 수 없다.")
    @Test
    void replyWithNoContentsIsNotPermitted() throws Exception {
        // given
        PostCommentRequestDto postComment = new PostCommentRequestDto(null, "");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/1/comments").with(csrf())
                        .content(objectMapper.writeValueAsString(postComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("댓글은 공백이 아닌 1자 이상이 포함되어야 합니다"));
    }

    @DisplayName("자유게시글의 댓글ID에 맞는 댓글 삭제를 요청한다.")
    @Test
    void deletePostCommentByCommentId() throws Exception {
        // given
        Mockito.doNothing().when(postService).deletePostComment(anyLong(), anyLong());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/1/comments/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("자유게시글의 댓글에 좋아요를 눌러서 좋아요를 요청한다.")
    @Test
    void sendLikeToPostComment() throws Exception {
        // given
        Mockito.doNothing().when(postService).sendLikeToPostComment(anyLong(), anyLong());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/1/comments/0/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }
}