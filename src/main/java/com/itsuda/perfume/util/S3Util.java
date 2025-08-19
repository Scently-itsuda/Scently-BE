package com.itsuda.perfume.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;

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
    public void uploadFile(byte[] file, String savePath, String fileName, String contentType) {
        RequestBody requestBody = RequestBody.fromBytes(file);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(savePath + fileName)
                .contentType(contentType).build();

        s3Client.putObject(putObjectRequest, requestBody);
    }

    @Async
    public void uploadFiles(List<byte[]> files, String savePath, List<String> fileNames, List<String> contentTypes) {
        for (int i = 0; i < files.size(); i++) {
            RequestBody requestBody = RequestBody.fromBytes(files.get(i));

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(savePath + fileNames.get(i))
                    .contentType(contentTypes.get(i)).build();

            s3Client.putObject(putObjectRequest, requestBody);
        }
    }
}
