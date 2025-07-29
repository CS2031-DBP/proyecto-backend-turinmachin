package com.turinmachin.unilife.thumbnail.domain;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public ByteArrayOutputStream generateThumbnailOutputStream(final InputStream inputStream) throws IOException {
        final var thumbnailOutput = new ByteArrayOutputStream();

        final BufferedImage image = ImageIO.read(inputStream);
        if (image == null)
            return null;

        final int thumbnailWidth = (int) (image.getWidth() * reduction);
        final int thumbnailHeight = (int) (image.getHeight() * reduction);

        Thumbnails.of(image)
                .size(Math.min(thumbnailWidth, maxSize), Math.min(thumbnailHeight, maxSize))
                .outputFormat("jpg")
                .outputQuality(quality)
                .toOutputStream(thumbnailOutput);

        return thumbnailOutput;

    }

    public String generateThumbnailDataUrl(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream outputStream = generateThumbnailOutputStream(inputStream);
        if (outputStream == null)
            return null;

        final byte[] thumbnailBytes = outputStream.toByteArray();

        if (thumbnailBytes == null)
            return null;

        final String thumbnailData = Base64.getEncoder().encodeToString(thumbnailBytes);
        return "data:image/jpeg;base64," + thumbnailData;
    }

    public String generateThumbnailDataUrl(final MultipartFile file) throws IOException {
        return generateThumbnailDataUrl(file.getInputStream());
    }

}
