package com.turinmachin.unilife.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdatePostDto {

    @NotBlank
    @Size(max = 300)
    private String content;

    @Size(max = 10)
    private List<String> tags = new ArrayList<>();

}
