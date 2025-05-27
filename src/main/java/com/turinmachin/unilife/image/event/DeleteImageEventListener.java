package com.turinmachin.unilife.image.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.turinmachin.unilife.storage.domain.StorageService;

@Component
@RequiredArgsConstructor
public class DeleteImageEventListener {

    private final StorageService storageService;

    @Async
    @EventListener
    public void handleDeleteImageEvent(DeleteImagesEvent event) {
        for (String key : event.getImageKeys()) {
            storageService.deleteFile(key);
        }
    }

}
