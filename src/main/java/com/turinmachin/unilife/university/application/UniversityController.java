package com.turinmachin.unilife.university.application;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.domain.DegreeService;
import com.turinmachin.unilife.degree.exception.DegreeNotFoundException;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.domain.UniversityService;
import com.turinmachin.unilife.university.dto.AddDegreeToUniversityDto;
import com.turinmachin.unilife.university.dto.CreateUniversityDto;
import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import com.turinmachin.unilife.university.dto.UpdateUniversityDto;
import com.turinmachin.unilife.university.exception.UniversityNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    private final DegreeService degreeService;

    private final ModelMapper modelMapper;

    @GetMapping
    public List<UniversityResponseDto> getUniversities() {
        List<University> universities = universityService.getAllActiveUniversities();
        return universities.stream().map(uni -> modelMapper.map(uni, UniversityResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public UniversityResponseDto getUniversity(@PathVariable UUID id) {
        University university = universityService.getActiveUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto createUniversity(@Valid @RequestBody CreateUniversityDto dto) {
        University university = universityService.createUniversity(dto);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto updateUniversity(@PathVariable UUID id, @Valid @RequestBody UpdateUniversityDto dto) {
        University university = universityService.getActiveUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);
        university = universityService.updateUniversity(university, dto);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @PatchMapping("/{id}/degrees")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto addUniversityDegree(@PathVariable UUID id,
            @Valid @RequestBody AddDegreeToUniversityDto dto) {
        University university = universityService.getActiveUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);
        Degree degree = degreeService.getDegreeById(dto.getDegreeId()).orElseThrow(DegreeNotFoundException::new);
        university = universityService.addDegreeToUniversity(university, degree);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @DeleteMapping("/{id}/degrees/{degreeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto removeUniversityDegree(@PathVariable UUID id,
            @PathVariable UUID degreeId) {
        University university = universityService.getActiveUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);
        Degree degree = degreeService.getDegreeById(degreeId).orElseThrow(DegreeNotFoundException::new);

        university = universityService.removeDegreeFromUniversity(university, degree);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUniversity(@PathVariable UUID id) {
        University university = universityService.getActiveUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);
        universityService.deactivateUniversity(university);
    }

}
