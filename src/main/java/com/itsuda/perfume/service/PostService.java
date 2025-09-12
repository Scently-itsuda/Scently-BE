package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Notification;
import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.PostTag;
import com.itsuda.perfume.domain.Tag;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserFcmToken;
import com.itsuda.perfume.domain.UserLikeComment;
import com.itsuda.perfume.domain.UserLikePost;
import com.itsuda.perfume.domain.type.NotificationType;
import com.itsuda.perfume.domain.type.PostOrderType;
import com.itsuda.perfume.dto.response.post.CommentsDto;
import com.itsuda.perfume.dto.response.post.CreatedPostDto;
import com.itsuda.perfume.dto.response.post.PostCommentDto;
import com.itsuda.perfume.dto.response.post.PostDetailDto;
import com.itsuda.perfume.dto.response.post.PostInfoDto;
import com.itsuda.perfume.dto.response.post.PostMainDto;
import com.itsuda.perfume.dto.response.post.UserInfoDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.NotificationRepository;
import com.itsuda.perfume.repository.PostRepository;
import com.itsuda.perfume.repository.PostTagRepository;
import com.itsuda.perfume.repository.TagRepository;
import com.itsuda.perfume.repository.UserFcmTokenRepository;
import com.itsuda.perfume.repository.UserLikeCommentRepository;
import com.itsuda.perfume.repository.UserLikePostRepository;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.repository.jdbctemplate.PostTagJdbcTemplateRepository;
import com.itsuda.perfume.repository.jdbctemplate.TagJdbcTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static com.itsuda.perfume.domain.type.NotificationType.*;
import static com.itsuda.perfume.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final TagJdbcTemplateRepository tagJdbcTemplateRepository;
    private final PostTagJdbcTemplateRepository postTagJdbcTemplateRepository;

    private final UserLikeCommentRepository userLikeCommentRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final NotificationRepository notificationRepository;
    private final UserLikePostRepository userLikePostRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    public PostMainDto getPostsByOrderType(int page, int size, PostOrderType postOrderType) {
        Pageable pageable = PageRequest.of(page, size, switch (postOrderType) {
            case NEWEST_DESCENDING -> Sort.by("createdAt").descending();
            case NEWEST_ASCENDING -> Sort.by("createdAt").ascending();
            case POPULAR_DESCENDING -> Sort.by("likeCount").descending();
            case POPULAR_ASCENDING -> Sort.by("likeCount").ascending();
        });

        return PostMainDto.from(postRepository.findAllByDeletedAtIsNull(pageable));
    }

    @Transactional
    public CreatedPostDto createPost(Long userId, String title, String content, List<String> tagNames) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Post post = postRepository.save(Post.builder().title(title).content(content).user(user).build());

        List<Tag> tags = tagNames.stream().map(tag -> Tag.builder().name(tag).build()).toList();
        tagJdbcTemplateRepository.batchInsert(tags);
        Long lastInsertedId = tagJdbcTemplateRepository.getLastInsertedId();

        List<Long> tagIds = LongStream.rangeClosed(lastInsertedId - tags.size() + 1, lastInsertedId).boxed().toList();
        postTagJdbcTemplateRepository.batchInsert(tagIds, post);

        return new CreatedPostDto(post.getId());
    }

    public PostDetailDto getPostDetailByPostId(Long postId) {
        Post post = validatePost(postRepository.findById(postId));
        User user = post.getUser();

        return PostDetailDto.from(post, user);
    }

    @Transactional
    public void deletePostByPostId(Long postId, Long userId) {
        Post post = validatePost(postRepository.findById(postId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        if (!post.getUser().getId().equals(user.getId())) {
            throw new RestApiException(ONLY_POST_OWNER_DELETE);
        }

        postRepository.delete(post);
    }

    public CommentsDto getCommentsByPostId(Long postId) {
        Post post = validatePost(postRepository.findById(postId));
        List<Comment> comments = commentRepository.findAllByPostAndParentCommentIsNull(post);

        return CommentsDto.from(comments);
    }

    @Transactional
    public void deletePostComment(Long commentId, Long userId) {
        Comment comment = validateComment(commentRepository.findById(commentId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RestApiException(ONLY_COMMENT_OWNER_DELETE);
        }

        commentRepository.delete(comment);
    }

    // TODO - 추후 처리율 제한과 비동기 처리 예정
    @Transactional
    public void sendLikeToPost(Long postId, Long userId) {
        Post post = validatePost(postRepository.findById(postId));
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
        sendFCMMessage(userFcmToken, user.getNickname() + "님이 회원님의 자유게시글을 추천합니다.", post.getContent(), user, post.getUser(), post.getId(), POST_LIKE);
    }

    @Transactional
    public PostCommentDto writeCommentToPost(Long postId, Long userId, Long commentId, String content) {
        Post post = validatePost(postRepository.findById(postId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(post.getUser());
        Optional<Comment> parentComment = Optional.ofNullable(commentId).flatMap(commentRepository::findById);

        Comment comment = commentRepository.save(Comment.builder().
                content(content)
                .parentComment(parentComment.orElse(null))
                .ootd(null)
                .post(post)
                .user(user)
                .build());

        sendFCMMessage(userFcmToken, user.getNickname() + "님이 " + post.getUser() + "님의 게시물에 댓글을 남겼습니다.", comment.getContent(), user, comment.getUser(), post.getId(), POST_COMMENT);
        return new PostCommentDto(comment.getId());
    }

    @Transactional
    public void sendLikeToPostComment(Long userId, Long commentId) {
        Comment comment = validateComment(commentRepository.findById(commentId));
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

    private Post validatePost(Optional<Post> optionalPost) {
        Post post = optionalPost.orElseThrow(() -> new RestApiException(NOT_FOUND_POST));
        if (Optional.ofNullable(post.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_POST);
        }
        return post;
    }

    private Comment validateComment(Optional<Comment> optionalComment) {
        Comment comment = optionalComment.orElseThrow(() -> new RestApiException(NOT_FOUND_COMMENT));
        if (Optional.ofNullable(comment.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_COMMENT);
        }
        return comment;
    }

    private void sendFCMMessage(Optional<UserFcmToken> userFcmToken, String title, String bodyMessage, User sender, User receiver, Long targetId, NotificationType notificationType) {
        userFcmToken.ifPresent(fcmToken -> {
                    Notification notification = notificationRepository.save(Notification.builder()
                            .title(title)
                            .bodyMessage(bodyMessage)
                            .notificationSender(sender)
                            .notificationReceiver(receiver)
                            .targetId(targetId)
                            .notificationType(notificationType).build());
                    fcmService.sendFCMMessage(notification.getTitle(), notification.getBodyMessage(), fcmToken.getFcmToken());
                }
        );
    }
}
