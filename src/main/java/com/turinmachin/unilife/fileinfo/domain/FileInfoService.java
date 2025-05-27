package com.turinmachin.unilife.fileinfo.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.turinmachin.unilife.common.exception.UnsupportedMediaTypeException;
import com.turinmachin.unilife.storage.domain.StorageService;
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

    public boolean isContentTypeValid(String contentType) {
        return contentType != null
                && (contentType.startsWith("image/")
                        || contentType.startsWith("video/"));
    }

}
