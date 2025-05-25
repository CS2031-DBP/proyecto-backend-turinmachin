package com.turinmachin.unilife.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePostDto {

    @NotBlank
    private String content;

    private List<String> tags = new ArrayList<>();

    private List<MultipartFile> images = new ArrayList<>();

}
