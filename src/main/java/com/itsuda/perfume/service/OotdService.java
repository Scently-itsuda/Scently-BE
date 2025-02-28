package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.type.OotdSortType;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.repository.OotdImageRepository;
import com.itsuda.perfume.repository.OotdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OotdService {

    private final OotdRepository ootdRepository;
    private final OotdImageRepository ootdImageRepository;

    public OotdMainDto getOotdThumbnailsBySort(int page, int size, OotdSortType ootdSortType) {
        // Todo 인기순 로직 개발

        return null;
    }
}
