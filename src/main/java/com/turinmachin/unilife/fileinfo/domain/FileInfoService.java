package com.turinmachin.unilife.fileinfo.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.turinmachin.unilife.storage.domain.StorageService;
import com.turinmachin.unilife.thumbnail.domain.ThumbnailService;
import com.turinmachin.unilife.common.exception.UnsupportedMediaTypeException;
import com.turinmachin.unilife.fileinfo.event.DeleteFilesEvent;
import com.turinmachin.unilife.fileinfo.infrastructure.FileInfoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FileInfoService {

    private final FileInfoRepository fileInfoRepository;

    private final StorageService storageService;

    private final ThumbnailService thumbnailService;

    private final ApplicationEventPublisher eventPublisher;

    public FileInfo createFile(MultipartFile file) throws IOException {
        if (!isContentTypeValid(file.getContentType())) {
            throw new UnsupportedMediaTypeException();
        }

        String key = storageService.uploadFile(file);
        String url = storageService.getObjectUrl(key).toString();

        FileInfo fileInfo = new FileInfo();
        fileInfo.setKey(key);
        fileInfo.setUrl(url);
        fileInfo.setMediaType(file.getContentType());

        if (isContentTypeImage(file.getContentType())) {
            String blurDataUrl = thumbnailService.generateThumbnailDataUrl(file);
            fileInfo.setBlurDataUrl(blurDataUrl);
        }

        return fileInfoRepository.save(fileInfo);
    }

    public List<FileInfo> createFileBatch(List<MultipartFile> files) throws IOException {
        if (!files.stream().map(MultipartFile::getContentType).allMatch(this::isContentTypeValid)) {
            throw new UnsupportedMediaTypeException();
        }

        List<FileInfo> fileInfos = new ArrayList<>(files.size());

        try {
            for (MultipartFile file : files) {
                FileInfo uploadedFile = createFile(file);
                fileInfos.add(uploadedFile);
            }
        } catch (IOException e) {
            // Remove partially uploaded files
            triggerFileBatchDeletion(fileInfos);
            throw e;
        }

        return fileInfos;
    }

    public void deleteFile(FileInfo file) {
        eventPublisher.publishEvent(new DeleteFilesEvent(List.of(file.getKey())));
        fileInfoRepository.delete(file);
    }

    public void triggerFileBatchDeletion(List<FileInfo> files) {
        eventPublisher.publishEvent(new DeleteFilesEvent(files.stream().map(FileInfo::getKey).toList()));
    }

    public boolean isContentTypeImage(String contentType) {
        return contentType.startsWith("image/");
    }

    public boolean isContentTypeVideo(String contentType) {
        return contentType.startsWith("video/");
    }

    public boolean isContentTypeValid(String contentType) {
        return contentType != null && (isContentTypeImage(contentType) || isContentTypeVideo(contentType));
    }

}
