package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikeOotd;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.OotdCommentDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdLikeDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.ootd.OotdThumbnailDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.CommentRepository;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.OotdRepository.OotdThumbnailInfo;
import com.itsuda.perfume.repository.UserLikeOotdRepository;
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

import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_OOTD;
import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OotdService {

    private final OotdRepository ootdRepository;
    private final UserRepository userRepository;
    private final UserLikeOotdRepository userLikeOotdRepository;
    private final CommentRepository commentRepository;

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

    public OotdDetailDto getOotdDetailByOotdId(Long ootdId, Long userId) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Boolean isLiked = userLikeOotdRepository.existsByUserAndOotd(user, ootd);

        return OotdDetailDto.from(ootd, ootd.getUser(), ootd.getPerfume(), isLiked);
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

        Optional<UserLikeOotd> userLikeOotd = userLikeOotdRepository.findByOotdAndUser(ootd, user);
        if (userLikeOotd.isPresent()) {
            userLikeOotdRepository.delete(userLikeOotd.get());
            ootd.decreaseLikeCount();
            return new OotdLikeDto(ootd.getId(), false);
        }

        userLikeOotdRepository.save(UserLikeOotd.builder().ootd(ootd).user(user).build());
        ootd.increaseLikeCount();
        return new OotdLikeDto(ootd.getId(), true);
    }

    @Transactional
    public OotdCommentDto writeCommentToOotd(Long ootdId, Long userId, Long commentId, String content) {
        Ootd ootd = ootdRepository.findById(ootdId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Optional<Comment> parentComment = Optional.ofNullable(commentId).flatMap(commentRepository::findById);

        Comment comment = commentRepository.save(Comment.builder().
                content(content)
                .likeCount(0)
                .parentComment(parentComment.orElse(null))
                .ootd(ootd)
                .post(null)
                .user(user)
                .build());

        return new OotdCommentDto(comment.getId());
    }
}
