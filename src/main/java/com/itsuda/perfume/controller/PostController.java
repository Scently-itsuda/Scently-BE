package com.itsuda.perfume.controller;

import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseDto<PostMainDto> getPosts(
            @RequestParam(required = false, defaultValue = "NEWEST") PostOrderType order,
            @RequestParam int page, @RequestParam int size) {
        return new ResponseDto<>(postService.getPostsByOrderType(page, size, order));
    }

    @GetMapping("/{postId}")
    public ResponseDto<PostDetailDto> getPostDetail(@PathVariable Long postId) {
        return new ResponseDto<>(postService.getPostDetailByPostId(postId));
    }
}
