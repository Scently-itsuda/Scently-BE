package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostDto;
import com.itsuda.perfume.dto.response.post.PostInfoDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.dto.response.post.UserInfoDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostMainDto getPostsByOrderType(int page, int size, PostOrderType postOrderType) {
        // Todo: PostOrderType 정해지는 대로 그에 맞는 정렬 로직 도입
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Post> posts = postRepository.findAll(pageable);
        List<PostDto> postDtos = posts.stream().map(PostDto::from).toList();
        return new PostMainDto(postDtos, PageInfoDto.from(posts));
    }

    public PostDetailDto getPostDetailByPostId(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUNT_POST));
        User user = post.getUser();

        return new PostDetailDto(PostInfoDto.from(post), UserInfoDto.from(user));
    }

    public CommentsDto getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUNT_POST));
        List<Comment> comments = commentRepository.findAllByPostAndParentCommentIsNull(post);

        return CommentsDto.from(comments);
    }
}
