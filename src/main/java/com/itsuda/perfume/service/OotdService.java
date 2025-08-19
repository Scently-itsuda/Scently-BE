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
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.CreatedOotdDto;
import com.itsuda.perfume.dto.response.ootd.OotdCommentDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.ootd.OotdThumbnailDto;
import com.itsuda.perfume.dto.response.ootd.UserLikeOotdsDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.NotificationRepository;
import com.itsuda.perfume.repository.OotdPerfumeRepository;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.OotdRepository.OotdThumbnailInfo;
import com.itsuda.perfume.repository.OotdRepository.UserLikeOotdInfo;
import com.itsuda.perfume.repository.PerfumeRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
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

    private final JdbcTemplate jdbcTemplate;

    @Value("${cloud.aws.s3.save-path.ootd-image}")
    private String OOTD_IMAGE_SAVE_PATH;

    public OotdMainDto getOotdThumbnailsByOrderType(int page, int size, OotdOrderType ootdOrderType, Long userId) {
        Pageable pageable = PageRequest.of(page, size,
                switch (ootdOrderType) {
                    case NEWEST_DESCENDING -> Sort.by("created_at").descending();
                    case NEWEST_ASCENDING -> Sort.by("created_at").ascending();
                    case POPULAR_DESCENDING -> Sort.by("like_count").descending();
                    case POPULAR_ASCENDING -> Sort.by("like_count").ascending();
                });

        Page<OotdThumbnailInfo> ootdThumbnails = ootdRepository.findAllIncludingUserLiked(pageable, userId);

        return new OotdMainDto(ootdThumbnails.stream().map(OotdThumbnailDto::from).toList(), PageInfoDto.from(ootdThumbnails));
    }

    @Transactional
    public CreatedOotdDto createOotd(Long userId, String content, List<String> tagNames, int volume,
                                     List<Long> perfumeId, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        List<Perfume> perfumes = perfumeRepository.findAllByIdIn(perfumeId);

        Ootd ootd = ootdRepository.save(Ootd.builder().likeCount(0).volume(volume).user(user).content(content).build());

        List<OotdImage> ootdImages = IntStream.range(0, images.size()).mapToObj(index -> OotdImage.builder()
                .ootd(ootd)
                .originName(images.get(index).getOriginalFilename())
                .saveName(UUID.randomUUID().toString() + FileUtil.getFileExtensionWithDot(images.get(index).getOriginalFilename()))
                .sequence(index).build()).toList();
        jdbcTemplate.batchUpdate("INSERT INTO ootd_image (origin_name, save_name, sequence, ootd_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?)", ootdImages, ootdImages.size(), (ps, ootdImage) -> {
            ps.setString(1, ootdImage.getOriginName());
            ps.setString(2, ootdImage.getSaveName());
            ps.setInt(3, ootdImage.getSequence());
            ps.setLong(4, ootdImage.getOotd().getId());
            ps.setDate(5, Date.valueOf(LocalDate.now()));
        });

        List<OotdPerfume> ootdPerfumes = perfumes.stream().map(perfume -> OotdPerfume.builder().ootd(ootd).perfume(perfume).build()).toList();
        jdbcTemplate.batchUpdate("INSERT INTO ootd_perfume (ootd_id, perfume_id) " +
                "VALUES (?, ?)", ootdPerfumes, ootdPerfumes.size(), (ps, ootdPerfume) -> {
            ps.setLong(1, ootdPerfume.getOotd().getId());
            ps.setLong(2, ootdPerfume.getPerfume().getId());
        });

        List<Tag> tags = tagNames.stream().map(tag -> Tag.builder().name(tag).build()).toList();
        jdbcTemplate.batchUpdate("INSERT INTO tag (name) VALUES (?)", tags, tags.size(), (ps, tag) -> ps.setString(1, tag.getName()));
        Long firstInsertedTagId = jdbcTemplate.queryForObject("SELECT last_insert_id()", Long.class);

        List<Long> tagIds = LongStream.rangeClosed(firstInsertedTagId - tags.size() + 1, firstInsertedTagId).boxed().toList();
        jdbcTemplate.batchUpdate("INSERT INTO ootd_tag (ootd_id, tag_id) VALUES (?, ?)", tagIds, tagIds.size(), (ps, tagId) -> {
            ps.setLong(1, ootd.getId());
            ps.setLong(2, tagId);
        });

        s3Util.uploadFiles(FileUtil.getFileBytes(images), OOTD_IMAGE_SAVE_PATH, ootdImages.stream().map(OotdImage::getSaveName).toList(), FileUtil.getContentTypes(images));

        return new CreatedOotdDto(ootd.getId());
    }

    public OotdDetailDto getOotdDetailByOotdId(Long ootdId, Long userId) {
        Ootd ootd = ootdRepository.findByIdWithOotdImagesAndOotdTags(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        if (Optional.ofNullable(ootd.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_OOTD);
        }
        List<OotdPerfume> ootdPerfumes = ootdPerfumeRepository.findByOotd(ootd);
        boolean isLiked = Optional.ofNullable(userId).map(usId -> {
            User user = userRepository.findById(usId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
            return userLikeOotdRepository.existsByUserAndOotd(user, ootd);
        }).orElse(false);

        return OotdDetailDto.from(ootd, ootd.getUser(), ootdPerfumes.stream().map(OotdPerfume::getPerfume).toList(), isLiked);
    }

    @Transactional
    public void deleteOotdByOotdId(Long ootdId, Long userId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        if (Optional.ofNullable(ootd.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_OOTD);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        if (!ootd.getUser().getId().equals(user.getId())) {
            throw new RestApiException(ONLY_OOTD_OWNER_DELETE);
        }

        ootdRepository.delete(ootd);
    }

    public CommentsDto getCommentsByOotdId(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        if (Optional.ofNullable(ootd.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_OOTD);
        }
        List<Comment> comments = commentRepository.findAllByOotdAndParentCommentIsNullAndDeletedAtIsNull(ootd);

        return CommentsDto.from(comments);
    }

    // TODO - 추후 처리율 제한과 비동기 처리 예정
    @Transactional
    public void sendLikeToOotd(Long ootdId, Long userId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        if (Optional.ofNullable(ootd.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_OOTD);
        }
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
        userFcmToken.ifPresent(fcmToken -> {
                    Notification notification = notificationRepository.save(
                            Notification.builder()
                                    .title(user.getNickname() + "님이 회원님의 OOTD를 추천합니다.")
                                    .bodyMessage(ootd.getContent())
                                    .notificationSender(user)
                                    .notificationReceiver(ootd.getUser())
                                    .targetId(ootd.getId())
                                    .notificationType(OOTD_LIKE)
                                    .build());
                    fcmService.sendFCMMessage(notification.getTitle(), notification.getBodyMessage(), fcmToken.getFcmToken());
                }
        );
    }

    @Transactional
    public OotdCommentDto writeCommentToOotd(Long ootdId, Long userId, Long commentId, String content) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        if (Optional.ofNullable(ootd.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_OOTD);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(ootd.getUser());
        Optional<Comment> parentComment = Optional.ofNullable(commentId).flatMap(commentRepository::findById);

        Comment comment = commentRepository.save(Comment.builder().
                content(content)
                .likeCount(0)
                .parentComment(parentComment.orElse(null))
                .ootd(ootd)
                .post(null)
                .user(user)
                .build());

        userFcmToken.ifPresent(fcmToken -> {
                    Notification notification = notificationRepository.save(
                            Notification.builder()
                                    .title(user.getNickname() + "님이 " + ootd.getUser() + "님의 게시물에 댓글을 남겼습니다.")
                                    .bodyMessage(comment.getContent())
                                    .notificationSender(user)
                                    .notificationReceiver(comment.getUser())
                                    .targetId(ootd.getId())
                                    .notificationType(OOTD_COMMENT)
                                    .build());
                    fcmService.sendFCMMessage(notification.getTitle(), notification.getBodyMessage(), fcmToken.getFcmToken());
                }
        );
        return new OotdCommentDto(comment.getId());
    }

    @Transactional
    public void deleteOotdComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RestApiException(NOT_FOUND_COMMENT));
        if (Optional.ofNullable(comment.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_COMMENT);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RestApiException(ONLY_COMMENT_OWNER_DELETE);
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void sendLikeToOotdComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RestApiException(NOT_FOUND_COMMENT));
        if (Optional.ofNullable(comment.getDeletedAt()).isPresent()) {
            throw new RestApiException(DELETED_COMMENT);
        }
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
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Page<UserLikeOotdInfo> userLikeOotdInfos = Page.empty();

        if (ootdOrderType.equals(OotdOrderType.NEWEST_DESCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
            userLikeOotdInfos = ootdRepository.findAllUserLikeByUser(pageable, userId);
        } else if (ootdOrderType.equals(OotdOrderType.NEWEST_ASCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").ascending());
            userLikeOotdInfos = ootdRepository.findAllUserLikeByUser(pageable, userId);
        } else if (ootdOrderType.equals(OotdOrderType.POPULAR_DESCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("like_count").descending());
            userLikeOotdInfos = ootdRepository.findAllUserLikeByUser(pageable, userId);
        } else if (ootdOrderType.equals(OotdOrderType.POPULAR_ASCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("like_count").ascending());
            userLikeOotdInfos = ootdRepository.findAllUserLikeByUser(pageable, userId);
        }

        return UserLikeOotdsDto.from(userLikeOotdInfos.getContent(), PageInfoDto.from(userLikeOotdInfos));
    }
}
