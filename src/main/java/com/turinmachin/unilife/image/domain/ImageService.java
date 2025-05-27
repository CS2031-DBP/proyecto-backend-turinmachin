package com.turinmachin.unilife.image.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.turinmachin.unilife.common.exception.UnsupportedMediaTypeException;
import com.turinmachin.unilife.storage.domain.StorageService;
import com.turinmachin.unilife.image.event.DeleteImagesEvent;
import com.turinmachin.unilife.image.infrastructure.ImageRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    private final StorageService storageService;

    private final ApplicationEventPublisher eventPublisher;

    public Image createImage(MultipartFile file) throws IOException {
        if (!isContentTypeImage(file.getContentType())) {
            throw new UnsupportedMediaTypeException();
        }

        String key = storageService.uploadFile(file);
        String url = storageService.getObjectUrl(key).toString();

        Image image = new Image();
        image.setKey(key);
        image.setUrl(url);
        return imageRepository.save(image);
    }

    public List<Image> createImageBatch(List<MultipartFile> files) throws IOException {
        if (!files.stream().map(MultipartFile::getContentType).allMatch(this::isContentTypeImage)) {
            throw new UnsupportedMediaTypeException();
        }

        List<Image> images = new ArrayList<>(files.size());

        try {
            for (MultipartFile file : files) {
                Image uploadedImage = createImage(file);
                images.add(uploadedImage);
            }
        } catch (IOException e) {
            // Remove partially uploaded images
            deleteImageBatch(images);
            throw e;
        }

        return images;
    }

    public void deleteImage(Image image) {
        eventPublisher.publishEvent(new DeleteImagesEvent(List.of(image.getKey())));
        imageRepository.delete(image);
    }

    public void deleteImageBatch(List<Image> images) {
        eventPublisher.publishEvent(new DeleteImagesEvent(images.stream().map(Image::getKey).toList()));

        for (Image image : images) {
            imageRepository.delete(image);
        }
    }

    public boolean isContentTypeImage(String contentType) {
        return contentType != null
                && (contentType.equals("image/png")
                        || contentType.equals("image/jpg")
                        || contentType.equals("image/jpeg"));
    }

}
