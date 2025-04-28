package com.itsuda.perfume.dto.request.ootd;

import com.itsuda.perfume.annotation.ValidImageFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ImageFilesDto(
        @ValidImageFile
        List<MultipartFile> images
) {
}
