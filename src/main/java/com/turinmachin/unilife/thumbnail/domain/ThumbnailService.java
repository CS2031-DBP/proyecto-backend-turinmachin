package com.turinmachin.unilife.thumbnail.domain;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    @Value("${thumbnail.max-size}")
    private int maxSize;

    @Value("${thumbnail.reduction}")
    private double reduction;

    @Value("${thumbnail.quality}")
    private double quality;

    public ByteArrayOutputStream generateThumbnailOutputStream(MultipartFile file) throws IOException {
        var thumbnailOutput = new ByteArrayOutputStream();

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null)
            return null;

        int thumbnailWidth = (int) (image.getWidth() * reduction);
        int thumbnailHeight = (int) (image.getHeight() * reduction);

        Thumbnails.of(image)
                .size(Math.min(thumbnailWidth, maxSize), Math.min(thumbnailHeight, maxSize))
                .outputFormat("jpg")
                .outputQuality(quality)
                .toOutputStream(thumbnailOutput);

        return thumbnailOutput;

    }

    public String generateThumbnailDataUrl(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = generateThumbnailOutputStream(file);
        if (outputStream == null)
            return null;

        byte[] thumbnailBytes = outputStream.toByteArray();

        if (thumbnailBytes == null)
            return null;

        String thumbnailData = Base64.getEncoder().encodeToString(thumbnailBytes);
        return "data:image/jpeg;base64," + thumbnailData;
    }
}
