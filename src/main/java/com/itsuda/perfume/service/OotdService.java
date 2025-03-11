package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.ootd.OotdThumbnailDto;
import com.itsuda.perfume.exception.RestApiException;
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

import static com.itsuda.perfume.exception.ErrorCode.NOT_FOUND_OOTD;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OotdService {

    private final OotdRepository ootdRepository;
    private final UserLikeOotdRepository userLikeOotdRepository;
    private final UserRepository userRepository;

    public OotdMainDto getOotdThumbnailsByOrderType(int page, int size, OotdOrderType ootdOrderType, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());

        Page<OotdThumbnailInfo> ootdThumbnailInfos = ootdRepository.findByOotdOrderByOotdCreatedAt(pageable, userId);
        List<OotdThumbnailDto> ootdThumbnails = ootdThumbnailInfos.stream().map(OotdThumbnailDto::from).toList();
        return new OotdMainDto(ootdThumbnails, PageInfoDto.from(ootdThumbnailInfos));
    }

    public OotdDetailDto getOotdDetailByOotdId(Long id, Long userId) {
        Ootd ootd = ootdRepository.findById(id).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        Boolean isLiked = userLikeOotdRepository.existsByUserAndOotd(userRepository.findById(userId).get(), ootd);

        return OotdDetailDto.from(ootd, ootd.getUser(), ootd.getPerfume(), isLiked);
    }
}
