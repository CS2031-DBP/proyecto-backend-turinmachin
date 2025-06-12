package com.turinmachin.unilife.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turinmachin.unilife.degree.dto.DegreeResponseDto;
import com.turinmachin.unilife.fileinfo.dto.FileInfoResponseDto;
import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {

    private UUID id;
    private UserResponseDto author;
    private String content;
    private List<FileInfoResponseDto> files;
    private UniversityResponseDto university;
    private DegreeResponseDto degree;
    private List<String> tags;
    private Integer score;
    private Instant createdAt;
    private Instant updatedAt;

    private Short currentVote;
}
