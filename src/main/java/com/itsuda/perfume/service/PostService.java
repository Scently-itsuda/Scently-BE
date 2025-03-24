package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikePost;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.PostCommentDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostDto;
import com.itsuda.perfume.dto.response.post.PostInfoDto;
import com.itsuda.perfume.dto.response.post.PostLikeDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.dto.response.post.UserInfoDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.PostRepository;
import com.itsuda.perfume.repository.UserLikePostRepository;
import com.itsuda.perfume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_USER;
import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUNT_POST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserLikePostRepository userLikePostRepository;

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

    // 추후 처리율 제한과 비동기 처리 예정
    @Transactional
    public PostLikeDto sendLikeToPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(NOT_FOUNT_POST));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));

        Optional<UserLikePost> userLikePost = userLikePostRepository.findByPostAndUser(post, user);
        if (userLikePost.isPresent()) {
            userLikePostRepository.delete(userLikePost.get());
            post.decreaseLikeCount();
            return new PostLikeDto(post.getId(), false);
        }

        userLikePostRepository.save(UserLikePost.builder().post(post).user(user).build());
        post.increaseLikeCount();
        return new PostLikeDto(post.getId(), true);
    }

    @Transactional
    public PostCommentDto writeCommentToPost(Long postId, Long userId, Long commentId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(NOT_FOUNT_POST));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<Comment> parentComment = Optional.ofNullable(commentId).flatMap(commentRepository::findById);

        Comment comment = commentRepository.save(Comment.builder().
                content(content)
                .likeCount(0)
                .parentComment(parentComment.orElse(null))
                .ootd(null)
                .post(post)
                .user(user)
                .build());

        return new PostCommentDto(comment.getId());
    }

}
