package com.turinmachin.unilife.storage.domain;

import java.io.IOException;
import java.net.URL;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.turinmachin.unilife.fileinfo.exception.EmptyFileException;
import com.turinmachin.unilife.fileinfo.exception.FileTooLargeException;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final AmazonS3 s3Client;

    @Value("${amazon-s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        if (file.getSize() > 5242880) {
            throw new FileTooLargeException();
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        long time = System.currentTimeMillis();
        String key = time + "_" + file.getOriginalFilename();

        s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
        return key;
    }

    public URL getObjectUrl(String key) {
        return s3Client.getUrl(bucketName, key);
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

}
