package com.turinmachin.unilife.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdatePostDto {

    @NotBlank
    private String content;

    private List<String> tags = new ArrayList<>();

}
