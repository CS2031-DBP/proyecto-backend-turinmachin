package com.turinmachin.unilife.user.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserProfilePictureDto {

    @NotNull
    private MultipartFile picture;

}
