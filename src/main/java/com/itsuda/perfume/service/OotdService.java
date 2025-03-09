package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.ootd.OotdThumbnailDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.OotdImageRepository;
import com.itsuda.perfume.repository.OotdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.itsuda.perfume.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OotdService {

    private final OotdRepository ootdRepository;
    private final OotdImageRepository ootdImageRepository;

    public OotdMainDto getOotdThumbnailsByOrderType(int page, int size, OotdOrderType ootdOrderType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<OotdImage> ootdImages = ootdImageRepository.findByOotdOrderByOotdCreatedAt(pageable);
        List<OotdThumbnailDto> ootdThumbnails = ootdImages.stream().map(OotdThumbnailDto::from).toList();
        return new OotdMainDto(ootdThumbnails, PageInfoDto.from(ootdImages));
    }

    public OotdDetailDto getOotdDetailByOotdId(Long id) {
        Ootd ootd = ootdRepository.findById(id).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        return new OotdDetailDto(ootd.getId(),
                ootd.getCreatedAt(),
                ootd.getOotdImages().stream().map(image -> image.getSaveName()).toList(),
                ootd.getLikeCount(),
                ootd.getCommentCount(),
                ootd.getUser().getGender().toString(),
                25,
                ootd.getVolume(),
                ootd.getContent(),
                ootd.getOotdTags().stream().map(tag -> tag.getTag().getName()).toList(),
                ootd.getPerfume().getId(),
                ootd.getPerfume().getBrand().toString(),
                ootd.getPerfume().getImageUri(),
                ootd.getPerfume().getName()
                );

    }
}
