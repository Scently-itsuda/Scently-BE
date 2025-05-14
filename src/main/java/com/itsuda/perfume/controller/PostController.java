package com.itsuda.perfume.controller;

import com.itsuda.perfume.annotation.UserId;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.request.post.CreatePostDto;
import com.itsuda.perfume.dto.request.post.PostCommentRequestDto;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.CreatedPostDto;
import com.itsuda.perfume.dto.response.post.PostCommentDto;
import com.itsuda.perfume.dto.response.post.PostCommentLikeDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostLikeDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Post", description = "자유게시판 관련 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "자유게시글 목록 조회", description = "자유게시판에 올라온 게시글들을 조회합니다.")
    @GetMapping
    public ResponseDto<PostMainDto> getPosts(
            @RequestParam(required = false, defaultValue = "NEWEST") PostOrderType order,
            @RequestParam int page, @RequestParam int size) {
        return new ResponseDto<>(postService.getPostsByOrderType(page, size, order));
    }

    @Operation(summary = "자유게시글 작성", description = "자유게시판에 게시글을 작성합니다.")
    @PostMapping
    public ResponseDto<CreatedPostDto> createPost(
            @UserId Long userId,
            @Valid @RequestBody CreatePostDto createPostDto) {
        return new ResponseDto<>(postService.createPost(
                userId, createPostDto.title(), createPostDto.content(), createPostDto.tagNames()));
    }

    @Operation(summary = "자유게시글 상세 조회", description = "자유게시판에서 게시글 ID에 맞는 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseDto<PostDetailDto> getPostDetail(@PathVariable Long postId) {
        return new ResponseDto<>(postService.getPostDetailByPostId(postId));
    }

    @Operation(summary = "자유게시글 좋아요", description = "자유게시판 게시글에 좋아요를 요청합니다.")
    @PostMapping("/{postId}/like")
    public ResponseDto<PostLikeDto> likePostByPostId(@UserId Long userId, @PathVariable Long postId) {
        return new ResponseDto<>(postService.sendLikeToPost(postId, userId));
    }

    @Operation(summary = "자유게시글 상세 댓글 조회", description = "자유게시판에서 게시글 ID에 맞는 댓글들을 조회합니다.")
    @GetMapping("/{postId}/comments")
    public ResponseDto<CommentsDto> getComments(@PathVariable Long postId) {
        return new ResponseDto<>(postService.getCommentsByPostId(postId));
    }

    @Operation(summary = "자유게시글 댓글 작성", description = "자유게시판의 게시글에 댓글을 답니다.")
    @PostMapping("/{postId}/comments")
    public ResponseDto<PostCommentDto> writeComment(
            @UserId Long userId, @PathVariable Long postId, @Valid @RequestBody PostCommentRequestDto postComment) {
        return new ResponseDto<>(postService.writeCommentToPost(postId, userId,
                postComment.commentId(), postComment.comment()));
    }

    @Operation(summary = "자유게시글 댓글 좋아요", description = "자유게시글의 댓글에 좋아요를 요청합니다.")
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseDto<PostCommentLikeDto> likePostCommentByCommentId(@UserId Long userId, @PathVariable Long commentId) {
        return new ResponseDto<>(postService.sendLikeToPostComment(userId, commentId));
    }
}
