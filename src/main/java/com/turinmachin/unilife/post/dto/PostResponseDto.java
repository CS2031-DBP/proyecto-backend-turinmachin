package com.turinmachin.unilife.post.dto;

import com.turinmachin.unilife.comment.dto.CommentResponseDto;
import com.turinmachin.unilife.degree.dto.DegreeResponseDto;
import com.turinmachin.unilife.fileinfo.dto.FileInfoResponseDto;
import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class PostResponseDto {

    private UUID id;
    private UserResponseDto author;
    private String content;
    private List<FileInfoResponseDto> files;
    private UniversityResponseDto university;
    private DegreeResponseDto degree;
    private List<String> tags;
    private List<CommentResponseDto> comments;
    private Integer score;
    private Instant createdAt;
    private Instant updatedAt;

    private Short currentVote;
}
