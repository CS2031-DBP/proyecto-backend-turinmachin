package com.turinmachin.unilife.image.event;

import java.util.List;

import lombok.Data;

@Data
public class DeleteImagesEvent {

    private final List<String> imageKeys;

}
