package com.turinmachin.unilife.university.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUniversityPictureDto {

    @NotNull
    private MultipartFile picture;

}
