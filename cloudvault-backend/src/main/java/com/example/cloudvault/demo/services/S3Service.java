package com.example.cloudvault.demo.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Objects;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {

        String contentType = Objects.requireNonNullElse(file.getContentType(), "application/octet-stream");

        // Upload file with correct content-type and public-read ACL
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName) // bucket name
                        .key(file.getOriginalFilename()) // name of the file in s3 bucket
                        .contentType(contentType) // important for browser display
                        .build(),
                RequestBody.fromBytes(file.getBytes()));
        GetUrlRequest request = GetUrlRequest.builder().bucket(bucketName).key(file.getOriginalFilename()).build();
        String url = s3Client.utilities().getUrl(request).toExternalForm();
     return url;
    }

    public byte[] downloadFile(String key) {
        ResponseBytes<GetObjectResponse> objectAsBytes =
                s3Client.getObjectAsBytes(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
        return objectAsBytes.asByteArray();
    }

    //delete File
    public void deleteFile(String key) {
        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key).build());
    }
}