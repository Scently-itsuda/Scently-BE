package com.itsuda.perfume.util;

import com.itsuda.perfume.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;

import static com.itsuda.perfume.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class S3Util {

    private static S3Client s3Client;
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucket;
    @Value("${cloud.aws.s3.bucket.expiration-time}")
    private Long expirationTime;

    @Autowired
    public S3Util(S3Client s3Client) {
        S3Util.s3Client = s3Client;
    }

    @Async
    public void uploadFile(MultipartFile file, String savePath, String fileName) {
        try {
            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket)
                    .key(savePath + fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, requestBody);
        } catch (IOException e) {
            throw new RestApiException(FILE_UPLOAD);
        }
    }

    @Async
    public void uploadFiles(List<MultipartFile> files, String savePath, List<String> fileNames) {
        for (int i = 0; i < files.size(); i++) {
            try {
                RequestBody requestBody = RequestBody.fromInputStream(files.get(i).getInputStream(), files.get(i).getSize());
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(savePath + fileNames.get(i))
                        .contentType(files.get(i).getContentType()).build();

                s3Client.putObject(putObjectRequest, requestBody);
            } catch (IOException e) {
                throw new RestApiException(FILE_UPLOAD);
            }
        }
    }
}
