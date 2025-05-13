package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdCommentNotification;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.OotdLikeNotification;
import com.itsuda.perfume.domain.OotdPerfume;
import com.itsuda.perfume.domain.OotdTag;
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
import com.itsuda.perfume.dto.response.ootd.OotdCommentLikeDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdLikeDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.ootd.OotdThumbnailDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.OotdCommentNotificationRepository;
import com.itsuda.perfume.repository.OotdImageRepository;
import com.itsuda.perfume.repository.OotdLikeNotificationRepository;
import com.itsuda.perfume.repository.OotdPerfumeRepository;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.OotdRepository.OotdThumbnailInfo;
import com.itsuda.perfume.repository.OotdTagRepository;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.TagRepository;
import com.itsuda.perfume.repository.UserFcmTokenRepository;
import com.itsuda.perfume.repository.UserLikeCommentRepository;
import com.itsuda.perfume.repository.UserLikeOotdRepository;
import com.itsuda.perfume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_COMMENT;
import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_OOTD;
import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OotdService {

    private final FcmService fcmService;

    private final OotdCommentNotificationRepository ootdCommentNotificationRepository;
    private final OotdLikeNotificationRepository ootdLikeNotificationRepository;
    private final UserLikeCommentRepository userLikeCommentRepository;
    private final UserLikeOotdRepository userLikeOotdRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final OotdPerfumeRepository ootdPerfumeRepository;
    private final OotdImageRepository ootdImageRepository;
    private final CommentRepository commentRepository;
    private final PerfumeRepository perfumeRepository;
    private final OotdTagRepository ootdTagRepository;
    private final OotdRepository ootdRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    // TODO - 추후 전략 패턴 도입
    public OotdMainDto getOotdThumbnailsByOrderType(int page, int size, OotdOrderType ootdOrderType, Long userId) {
        Page<OotdThumbnailInfo> ootdThumbnailInfos = Page.empty();
        List<OotdThumbnailDto> ootdThumbnails = List.of();

        if (ootdOrderType.equals(OotdOrderType.NEWEST_DESCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
            ootdThumbnailInfos = ootdRepository.findAllIncludingUserLiked(pageable, userId);
            ootdThumbnails = ootdThumbnailInfos.stream().map(OotdThumbnailDto::from).toList();
        } else if (ootdOrderType.equals(OotdOrderType.NEWEST_ASCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").ascending());
            ootdThumbnailInfos = ootdRepository.findAllIncludingUserLiked(pageable, userId);
            ootdThumbnails = ootdThumbnailInfos.stream().map(OotdThumbnailDto::from).toList();
        } else if (ootdOrderType.equals(OotdOrderType.POPULAR_DESCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("like_count").descending());
            ootdThumbnailInfos = ootdRepository.findAllIncludingUserLiked(pageable, userId);
            ootdThumbnails = ootdThumbnailInfos.stream().map(OotdThumbnailDto::from).toList();
        } else if (ootdOrderType.equals(OotdOrderType.POPULAR_ASCENDING)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("like_count").ascending());
            ootdThumbnailInfos = ootdRepository.findAllIncludingUserLiked(pageable, userId);
            ootdThumbnails = ootdThumbnailInfos.stream().map(OotdThumbnailDto::from).toList();
        }

        return new OotdMainDto(ootdThumbnails, PageInfoDto.from(ootdThumbnailInfos));
    }

    @Transactional
    public CreatedOotdDto createOotd(Long userId, String content, List<String> tagNames, int volume, List<Long> perfumeId,
                                     List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        List<Perfume> perfumes = perfumeRepository.findByIdIn(perfumeId);

        AtomicInteger atomicInt = new AtomicInteger(0);
        Ootd ootd = ootdRepository.save(Ootd.builder()
                .likeCount(0)
                .volume(volume)
                .user(user)
                .content(content).build());
        ootdImageRepository.saveAll(images.stream().map(image -> OotdImage.builder()
                .ootd(ootd)
                .originName(image.getOriginalFilename())
                .saveName(UUID.randomUUID().toString())
                .sequence(atomicInt.getAndIncrement()).build()).toList());
        ootdPerfumeRepository.saveAll(perfumes.stream().map(perfume -> OotdPerfume.builder().ootd(ootd)
                .perfume(perfume).build()).toList());
        List<Tag> savedTags = tagRepository.saveAll(tagNames.stream().map(tag -> Tag.builder().name(tag).build()).toList());

        ootdTagRepository.saveAll(savedTags.stream().map(tag -> OotdTag.builder().ootd(ootd).tag(tag).build()).toList());

        return new CreatedOotdDto(ootd.getId());
    }

    public OotdDetailDto getOotdDetailByOotdId(Long ootdId, Long userId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        List<OotdPerfume> ootdPerfumes = ootdPerfumeRepository.findByOotd(ootd);
        boolean isLiked = Optional.ofNullable(userId).map(usId -> {
            User user = userRepository.findById(usId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
            return userLikeOotdRepository.existsByUserAndOotd(user, ootd);
        }).orElse(false);

        return OotdDetailDto.from(ootd, ootd.getUser(), ootdPerfumes.stream().map(OotdPerfume::getPerfume).toList(), isLiked);
    }

    public CommentsDto getCommentsByOotdId(Long ootdId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        List<Comment> comments = commentRepository.findAllByOotdAndParentCommentIsNull(ootd);

        return CommentsDto.from(comments);
    }

    // TODO - 추후 처리율 제한과 비동기 처리 예정
    @Transactional
    public OotdLikeDto sendLikeToOotd(Long ootdId, Long userId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<UserFcmToken> userFcmToken = userFcmTokenRepository.findByUser(ootd.getUser());

        Optional<UserLikeOotd> userLikeOotd = userLikeOotdRepository.findByOotdAndUser(ootd, user);
        if (userLikeOotd.isPresent()) {
            userLikeOotdRepository.delete(userLikeOotd.get());
            ootd.decreaseLikeCount();
            return new OotdLikeDto(ootd.getId(), false);
        }

        userLikeOotdRepository.save(UserLikeOotd.builder().ootd(ootd).user(user).build());
        ootd.increaseLikeCount();
        userFcmToken.ifPresent(fcmToken -> {
                    OotdLikeNotification notification = ootdLikeNotificationRepository.save(
                            OotdLikeNotification.builder()
                                    .title(user.getNickname() + "님이 회원님의 OOTD를 추천합니다.")
                                    .bodyMessage(ootd.getContent())
                                    .likeSender(user)
                                    .likeReceiver(ootd.getUser())
                                    .ootd(ootd)
                                    .build());
                    fcmService.sendFCMMessage(notification.getTitle(), notification.getBodyMessage(), fcmToken.getFcmToken());
                }
        );
        return new OotdLikeDto(ootd.getId(), true);
    }

    @Transactional
    public OotdCommentDto writeCommentToOotd(Long ootdId, Long userId, Long commentId, String content) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
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
                    OotdCommentNotification notification = ootdCommentNotificationRepository.save(
                            OotdCommentNotification.builder()
                                    .title(user.getNickname() + "님이 " + ootd.getUser() + "님의 게시물에 댓글을 남겼습니다.")
                                    .bodyMessage(comment.getContent())
                                    .commentWriter(user)
                                    .commentReceiver(comment.getUser())
                                    .ootd(ootd)
                                    .build());
                    fcmService.sendFCMMessage(notification.getTitle(), notification.getBodyMessage(), fcmToken.getFcmToken());
                }
        );
        return new OotdCommentDto(comment.getId());
    }

    @Transactional
    public OotdCommentLikeDto sendLikeToOotdComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RestApiException(NOT_FOUND_COMMENT));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));

        Optional<UserLikeComment> userLikeComment = userLikeCommentRepository.findByCommentAndUser(comment, user);
        if (userLikeComment.isPresent()) {
            userLikeCommentRepository.delete(userLikeComment.get());
            return new OotdCommentLikeDto(false, comment.decreaseLikeCount());
        }

        userLikeCommentRepository.save(UserLikeComment.builder().comment(comment).user(user).build());
        return new OotdCommentLikeDto(true, comment.increaseLikeCount());
    }
}
