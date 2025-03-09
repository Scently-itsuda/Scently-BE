package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.ootd.OotdThumbnailDto;
import com.itsuda.perfume.repository.OotdImageRepository;
import com.itsuda.perfume.repository.OotdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return null;
    }
}
