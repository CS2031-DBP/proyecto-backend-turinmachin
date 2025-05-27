package com.turinmachin.unilife.user.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.turinmachin.unilife.degree.dto.DegreeResponsePartialDto;
import com.turinmachin.unilife.fileinfo.dto.FileInfoResponseDto;
import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import com.turinmachin.unilife.user.domain.Role;

import lombok.Data;

@Data
public class UserResponseDto {

    private UUID id;
    private String email;
    private String username;
    private String displayName;
    private String bio;
    private UniversityResponseDto university;
    private DegreeResponsePartialDto degree;
    private FileInfoResponseDto profilePicture;
    private Role role;
    private Integer streak;
    private LocalDate lastStreakDate;
    private boolean verified;
    private Instant createdAt;

}
