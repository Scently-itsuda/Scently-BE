package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Notification;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.OotdPerfume;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.Tag;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserFcmToken;
import com.itsuda.perfume.domain.UserLikeComment;
import com.itsuda.perfume.domain.UserLikeOotd;
import com.itsuda.perfume.domain.type.NotificationType;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.CreatedOotdDto;
import com.itsuda.perfume.dto.response.ootd.OotdCommentDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.ootd.UserLikeOotdsDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.NotificationRepository;
import com.itsuda.perfume.repository.jdbctemplate.OotdImageJdbcTemplateRepository;
import com.itsuda.perfume.repository.jdbctemplate.OotdPerfumeJdbcTemplateRepository;
import com.itsuda.perfume.repository.OotdPerfumeRepository;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.OotdRepository.UserLikeOotdInfo;
import com.itsuda.perfume.repository.jdbctemplate.OotdTagJdbcTemplateRepository;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.jdbctemplate.TagJdbcTemplateRepository;
import com.itsuda.perfume.repository.UserFcmTokenRepository;
import com.itsuda.perfume.repository.UserLikeCommentRepository;
import com.itsuda.perfume.repository.UserLikeOotdRepository;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.util.FileUtil;
import com.itsuda.perfume.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.itsuda.perfume.domain.type.NotificationType.*;
import static com.itsuda.perfume.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OotdService {

    private final OotdPerfumeJdbcTemplateRepository ootdPerfumeJdbcTemplateRepository;
    private final OotdImageJdbcTemplateRepository ootdImageJdbcTemplateRepository;
    private final OotdTagJdbcTemplateRepository ootdTagJdbcTemplateRepository;
    private final TagJdbcTemplateRepository tagJdbcTemplateRepository;

    private final UserLikeCommentRepository userLikeCommentRepository;
    private final UserLikeOotdRepository userLikeOotdRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final NotificationRepository notificationRepository;
    private final OotdPerfumeRepository ootdPerfumeRepository;
    private final CommentRepository commentRepository;
    private final PerfumeRepository perfumeRepository;
    private final OotdRepository ootdRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    private final S3Util s3Util;

    @Value("${cloud.aws.s3.save-path.ootd-image}")
    private String OOTD_IMAGE_SAVE_PATH;

    public OotdMainDto getOotdThumbnailsByOrderType(int page, int size, OotdOrderType ootdOrderType, Long userId) {
        Pageable pageable = PageRequest.of(page, size, switch (ootdOrderType) {
            case NEWEST_DESCENDING -> Sort.by("created_at").descending();
            case NEWEST_ASCENDING -> Sort.by("created_at").ascending();
            case POPULAR_DESCENDING -> Sort.by("like_count").descending();
            case POPULAR_ASCENDING -> Sort.by("like_count").ascending();
        });

        return OotdMainDto.from(ootdRepository.findAllIncludingUserLiked(pageable, userId));
    }

    @Transactional
    public CreatedOotdDto createOotd(Long userId, String content, List<String> tagNames, int volume,
                                     List<Long> perfumeIds, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        List<Perfume> perfumes = perfumeRepository.findAllByIdIn(perfumeIds);

        Ootd ootd = ootdRepository.save(Ootd.builder().volume(volume).user(user).content(content).build());

        List<OotdImage> ootdImages = IntStream.range(0, images.size()).mapToObj(index -> OotdImage.builder()
                .ootd(ootd)
                .originName(images.get(index).getOriginalFilename())
                .saveName(FileUtil.getRandomFileName(images.get(index).getOriginalFilename()))
                .sequence(index).build()).toList();
        ootdImageJdbcTemplateRepository.batchInsert(ootdImages);

        ootdPerfumeJdbcTemplateRepository.batchInsert(perfumes.stream().map(perfume -> OotdPerfume.builder().ootd(ootd).perfume(perfume).build()).toList());

        List<Tag> tags = tagNames.stream().map(tag -> Tag.builder().name(tag).build()).toList();
        tagJdbcTemplateRepository.batchInsert(tags);
        Long lastInsertedId = tagJdbcTemplateRepository.getLastInsertedId();

        List<Long> tagIds = LongStream.rangeClosed(lastInsertedId - tags.size() + 1, lastInsertedId).boxed().toList();
        ootdTagJdbcTemplateRepository.batchInsert(tagIds, ootd);

        s3Util.uploadFiles(FileUtil.getFileBytes(images), OOTD_IMAGE_SAVE_PATH,
                ootdImages.stream().map(OotdImage::getSaveName).toList(), FileUtil.getContentTypes(images));

        return new CreatedOotdDto(ootd.getId());
    }

    public OotdDetailDto getOotdDetailByOotdId(Long ootdId, Long userId) {
        Ootd ootd = validateOotd(ootdRepository.findByIdWithOotdImages(ootdId));

        List<OotdPerfume> ootdPerfumes = ootdPerfumeRepository.findByOotd(ootd);
        boolean isLiked = Optional.ofNullable(userId).map(ootdUserId -> {
            User user = userRepository.findById(ootdUserId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
            return userLikeOotdRepository.existsByUserAndOotd(user, ootd);
        }).orElse(false);

        return OotdDetailDto.from(ootd, ootdPerfumes, isLiked);
    }

    @Transactional
    public void deleteOotdByOotdId(Long ootdId, Long userId) {
        Ootd ootd = validateOotd(ootdRepository.findById(ootdId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        if (!ootd.getUser().getId().equals(user.getId())) {
            throw new RestApiException(ONLY_OOTD_OWNER_DELETE);
        }

        ootdRepository.delete(ootd);
    }

    public CommentsDto getCommentsByOotdId(Long ootdId) {
        Ootd ootd = validateOotd(ootdRepository.findById(ootdId));
        List<Comment> comments = commentRepository.findAllByOotdAndParentCommentIsNullAndDeletedAtIsNull(ootd);

        return CommentsDto.from(comments);
    }

    @Async
    @Transactional
    public void sendLikeToOotd(Long ootdId, Long userId) {
        Ootd ootd = validateOotd(ootdRepository.findById(ootdId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(ootd.getUser());

        Optional<UserLikeOotd> userLikeOotd = userLikeOotdRepository.findByOotdAndUser(ootd, user);
        if (userLikeOotd.isPresent()) {
            userLikeOotdRepository.delete(userLikeOotd.get());
            ootd.decreaseLikeCount();
            return;
        }

        userLikeOotdRepository.save(UserLikeOotd.builder().ootd(ootd).user(user).build());
        ootd.increaseLikeCount();
        sendFCMMessage(userFcmToken, user.getNickname() + "님이 회원님의 OOTD를 추천합니다.", ootd.getContent(), user, ootd.getUser(), ootd.getId(), OOTD_LIKE);
    }

    @Transactional
    public OotdCommentDto writeCommentToOotd(Long ootdId, Long userId, Long parentCommentId, String content) {
        Ootd ootd = validateOotd(ootdRepository.findByIdWithUser(ootdId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(ootd.getUser());
        Optional<Comment> parentComment = Optional.ofNullable(parentCommentId).flatMap(commentRepository::findById);

        Comment comment = commentRepository.save(Comment.builder()
                .content(content)
                .parentComment(parentComment.orElse(null))
                .ootd(ootd)
                .post(null)
                .user(user)
                .build());
        sendFCMMessage(userFcmToken, user.getNickname() + "님이 " + ootd.getUser() + "님의 게시물에 댓글을 남겼습니다.", comment.getContent(), user, comment.getUser(), ootd.getId(), OOTD_COMMENT);
        return new OotdCommentDto(comment.getId());
    }

    @Transactional
    public void deleteOotdComment(Long userId, Long commentId) {
        Comment comment = validateComment(commentRepository.findByIdWithUser(commentId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RestApiException(ONLY_COMMENT_OWNER_DELETE);
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void sendLikeToOotdComment(Long userId, Long commentId) {
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

    public UserLikeOotdsDto getAllUserLikeOotdsByOrderType(int page, int size, OotdOrderType ootdOrderType, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RestApiException(NOT_FOUND_USER);
        }

        Pageable pageable = PageRequest.of(page, size,
                switch (ootdOrderType) {
                    case NEWEST_DESCENDING -> Sort.by("created_at").descending();
                    case NEWEST_ASCENDING -> Sort.by("created_at").ascending();
                    case POPULAR_DESCENDING -> Sort.by("like_count").descending();
                    case POPULAR_ASCENDING -> Sort.by("like_count").ascending();
                });
        Page<UserLikeOotdInfo> userLikeOotdInfos = ootdRepository.findAllUserLikeByUser(pageable, userId);
        return UserLikeOotdsDto.from(userLikeOotdInfos.getContent(), PageInfoDto.from(userLikeOotdInfos));
    }

    private Ootd validateOotd(Optional<Ootd> optionalOotd) {
        Ootd ootd = optionalOotd.orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        if (Optional.ofNullable(ootd.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_OOTD);
        }
        return ootd;
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
