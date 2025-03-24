package com.itsuda.perfume.controller;

import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostLikeDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Post", description = "자유게시판 관련 API")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "자유게시판에 올라온 게시글들을 조회합니다.")
    public ResponseDto<PostMainDto> getPosts(
            @RequestParam(required = false, defaultValue = "NEWEST") PostOrderType order,
            @RequestParam int page, @RequestParam int size) {
        return new ResponseDto<>(postService.getPostsByOrderType(page, size, order));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "자유게시판에서 게시글 ID에 맞는 게시글을 조회합니다.")
    public ResponseDto<PostDetailDto> getPostDetail(@PathVariable Long postId) {
        return new ResponseDto<>(postService.getPostDetailByPostId(postId));
    }

    @Operation(summary = "자유게시판 게시글 좋아요 오청", description = "자유게시판 게시글에 좋아요를 요청합니다.")
    @PostMapping("/{postId}/like")
    public ResponseDto<PostLikeDto> likePostByPostId(@PathVariable Long postId) {
        return new ResponseDto<>(postService.sendLikeToPost(postId, 0L));
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "게시글 상세 댓글 조회", description = "자유게시판에서 게시글 ID에 맞는 게시글을 조회합니다.")
    public ResponseDto<CommentsDto> getComments(@PathVariable Long postId) {
        return new ResponseDto<>(postService.getCommentsByPostId(postId));
    }
}
