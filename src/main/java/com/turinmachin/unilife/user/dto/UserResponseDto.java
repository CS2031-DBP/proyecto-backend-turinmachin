package com.turinmachin.unilife.user.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turinmachin.unilife.degree.dto.DegreeResponseDto;
import com.turinmachin.unilife.fileinfo.dto.FileInfoResponseDto;
import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import com.turinmachin.unilife.user.domain.Role;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private UUID id;
    private String email;
    private String username;
    private String displayName;
    private String bio;
    private LocalDate birthday;
    private UniversityResponseDto university;
    private DegreeResponseDto degree;
    private FileInfoResponseDto profilePicture;
    private Role role;
    private Integer streak;
    private LocalDate lastStreakDate;
    private boolean verified;
    private Instant createdAt;

}
