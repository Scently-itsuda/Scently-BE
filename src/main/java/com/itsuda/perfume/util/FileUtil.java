package com.itsuda.perfume.util;

import com.itsuda.perfume.exception.RestApiException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.itsuda.perfume.exception.ErrorCode.*;

public class FileUtil {

    public static String getRandomFileName(String fileName) {
        return UUID.randomUUID() + getFileExtensionWithDot(fileName);
    }

    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static String getFileExtensionWithDot(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    public static List<byte[]> getFileBytes(List<MultipartFile> files) {
        return files.stream().map(file -> {
            try {
                return file.getBytes();
            } catch (IOException e) {
                throw new RestApiException(FILE_PROCESSING_FAIL);
            }
        }).toList();
    }

    public static List<String> getContentTypes(List<MultipartFile> files) {
        return files.stream().map(MultipartFile::getContentType).toList();
    }
}
