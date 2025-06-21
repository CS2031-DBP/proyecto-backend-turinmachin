package com.turinmachin.unilife.thumbnail.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    @Value("${thumbnail.size}")
    private int thumbnailSize;

    public ByteArrayOutputStream generateThumbnailOutputStream(MultipartFile file) throws IOException {
        var thumbnailOutput = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(180, 180)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toOutputStream(thumbnailOutput);

        return thumbnailOutput;

    }

    public String generateThumbnailDataUrl(MultipartFile file) throws IOException {
        byte[] thumbnailBytes = generateThumbnailOutputStream(file).toByteArray();
        String thumbnailData = Base64.getEncoder().encodeToString(thumbnailBytes);
        return "data:image/jpeg;base64" + thumbnailData;
    }
}
