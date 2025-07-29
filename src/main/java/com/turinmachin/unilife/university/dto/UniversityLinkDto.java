package com.turinmachin.unilife.university.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UniversityLinkDto {
    private UUID id;
    private String name;
    private String shortName;
}
