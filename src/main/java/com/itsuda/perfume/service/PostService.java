package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.PostCommentNotification;
import com.itsuda.perfume.domain.PostLikeNotification;
import com.itsuda.perfume.domain.PostTag;
import com.itsuda.perfume.domain.Tag;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserFcmToken;
import com.itsuda.perfume.domain.UserLikeComment;
import com.itsuda.perfume.domain.UserLikePost;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.CreatedPostDto;
import com.itsuda.perfume.dto.response.post.PostCommentDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostDto;
import com.itsuda.perfume.dto.response.post.PostInfoDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.dto.response.post.UserInfoDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.PostCommentNotificationRepository;
import com.itsuda.perfume.repository.PostLikeNotificationRepository;
import com.itsuda.perfume.repository.PostRepository;
import com.itsuda.perfume.repository.PostTagRepository;
import com.itsuda.perfume.repository.TagRepository;
import com.itsuda.perfume.repository.UserFcmTokenRepository;
import com.itsuda.perfume.repository.UserLikeCommentRepository;
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

import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_COMMENT;
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
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final FcmService fcmService;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final PostLikeNotificationRepository postLikeNotificationRepository;
    private final PostCommentNotificationRepository postCommentNotificationRepository;
    private final UserLikeCommentRepository userLikeCommentRepository;

    // TODO - 추후 전략 패턴 도입
    public PostMainDto getPostsByOrderType(int page, int size, PostOrderType postOrderType) {
        Page<Post> posts = Page.empty();
        List<PostDto> postDtos = List.of();

        if (postOrderType.equals(PostOrderType.NEWEST_DESCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            posts = postRepository.findAll(pageable);
            postDtos = posts.stream().map(PostDto::from).toList();
        } else if (postOrderType.equals(PostOrderType.NEWEST_ASCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
            posts = postRepository.findAll(pageable);
            postDtos = posts.stream().map(PostDto::from).toList();
        } else if (postOrderType.equals(PostOrderType.POPULAR_DESCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("likeCount").descending());
            posts = postRepository.findAll(pageable);
            postDtos = posts.stream().map(PostDto::from).toList();
        } else if (postOrderType.equals(PostOrderType.POPULAR_ASCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("likeCount").ascending());
            posts = postRepository.findAll(pageable);
            postDtos = posts.stream().map(PostDto::from).toList();
        }

        return new PostMainDto(postDtos, PageInfoDto.from(posts));
    }

    @Transactional
    public CreatedPostDto createPost(Long userId, String title, String content, List<String> tagNames) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Post post = postRepository.save(Post.builder().title(title).content(content).user(user).build());
        List<Tag> savedTags = tagRepository.saveAll(tagNames.stream()
                .map(tag -> Tag.builder().name(tag).build()).toList());
        postTagRepository.saveAll(savedTags.stream()
                .map(savedTag -> PostTag.builder().post(post).tag(savedTag).build()).toList());

        return new CreatedPostDto(post.getId());
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

    // TODO - 추후 처리율 제한과 비동기 처리 예정
    @Transactional
    public void sendLikeToPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(NOT_FOUNT_POST));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(post.getUser());

        Optional<UserLikePost> userLikePost = userLikePostRepository.findByPostAndUser(post, user);
        if (userLikePost.isPresent()) {
            userLikePostRepository.delete(userLikePost.get());
            post.decreaseLikeCount();
            return;
        }

        userLikePostRepository.save(UserLikePost.builder().post(post).user(user).build());
        post.increaseLikeCount();
        userFcmToken.ifPresent(fcmToken -> {
                    PostLikeNotification notification = postLikeNotificationRepository.save(
                            PostLikeNotification.builder()
                                    .title(user.getNickname() + "님이 회원님의 OOTD를 추천합니다.")
                                    .bodyMessage(post.getContent())
                                    .likeSender(user)
                                    .likeReceiver(post.getUser())
                                    .post(post)
                                    .build());
                    fcmService.sendFCMMessage(notification.getTitle(), notification.getBodyMessage(), fcmToken.getFcmToken());
                }
        );
    }

    @Transactional
    public PostCommentDto writeCommentToPost(Long postId, Long userId, Long commentId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(NOT_FOUNT_POST));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(post.getUser());
        Optional<Comment> parentComment = Optional.ofNullable(commentId).flatMap(commentRepository::findById);

        Comment comment = commentRepository.save(Comment.builder().
                content(content)
                .likeCount(0)
                .parentComment(parentComment.orElse(null))
                .ootd(null)
                .post(post)
                .user(user)
                .build());

        userFcmToken.ifPresent(fcmToken -> {
                    PostCommentNotification notification = postCommentNotificationRepository.save(
                            PostCommentNotification.builder()
                                    .title(user.getNickname() + "님이 " + post.getUser() + "님의 게시물에 댓글을 남겼습니다.")
                                    .bodyMessage(comment.getContent())
                                    .commentWriter(user)
                                    .commentReceiver(comment.getUser())
                                    .post(post)
                                    .build());
                    fcmService.sendFCMMessage(notification.getTitle(), notification.getBodyMessage(), fcmToken.getFcmToken());
                }
        );
        return new PostCommentDto(comment.getId());
    }

    @Transactional
    public void sendLikeToPostComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RestApiException(NOT_FOUND_COMMENT));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));

        Optional<UserLikeComment> userLikeComment = userLikeCommentRepository.findByCommentAndUser(comment, user);
        if (userLikeComment.isPresent()) {
            userLikeCommentRepository.delete(userLikeComment.get());
            comment.decreaseLikeCount();
            return;
        }

        userLikeCommentRepository.save(UserLikeComment.builder().comment(comment).user(user).build());
        comment.increaseLikeCount();
    }
}
