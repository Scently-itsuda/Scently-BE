package com.itsuda.perfume.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.itsuda.perfume.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.itsuda.perfume.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${cloud.aws.s3.bucket.name}")
    @Setter
    private static String bucket;
    private static AmazonS3 amazonS3;

    @Autowired
    public S3Util(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public static void uploadFile(MultipartFile file, String savePath, String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, savePath + fileName,
                    file.getInputStream(), objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new RestApiException(FILE_UPLOAD);
        }
    }
}
