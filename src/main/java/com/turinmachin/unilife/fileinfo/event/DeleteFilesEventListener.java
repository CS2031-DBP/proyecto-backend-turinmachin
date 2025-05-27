package com.turinmachin.unilife.fileinfo.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.turinmachin.unilife.storage.domain.StorageService;

@Component
@RequiredArgsConstructor
public class DeleteFilesEventListener {

    private final StorageService storageService;

    @Async
    @EventListener
    public void handleDeleteFilesEvent(DeleteFilesEvent event) {
        for (String key : event.getFileKeys()) {
            storageService.deleteFile(key);
        }
    }

}
