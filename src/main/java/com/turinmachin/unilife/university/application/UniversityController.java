package com.turinmachin.unilife.university.application;

import com.turinmachin.unilife.common.domain.ListMapper;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.domain.DegreeService;
import com.turinmachin.unilife.degree.exception.DegreeNotFoundException;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.domain.UniversityService;
import com.turinmachin.unilife.university.dto.AddDegreeToUniversityDto;
import com.turinmachin.unilife.university.dto.CreateUniversityDto;
import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import com.turinmachin.unilife.university.exception.UniversityNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/universities")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @Autowired
    private DegreeService degreeService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    @GetMapping
    public List<UniversityResponseDto> getUniversities() {
        List<University> universities = universityService.getAllUniversities();
        return listMapper.map(universities, UniversityResponseDto.class).toList();
    }

    @GetMapping("/{id}")
    public UniversityResponseDto getUniversity(@PathVariable UUID id) {
        University university = universityService.getUniversityById(id).orElseThrow(UniversityNotFoundException::new);
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
    public UniversityResponseDto updateUniversity(@Valid @RequestBody CreateUniversityDto dto) {
        University university = universityService.createUniversity(dto);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @PatchMapping("/{id}/degrees")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto addUniversityDegree(@PathVariable UUID id,
            @Valid @RequestBody AddDegreeToUniversityDto dto) {
        University university = universityService.getUniversityById(id).orElseThrow(UniversityNotFoundException::new);
        Degree degree = degreeService.getDegreeById(dto.getDegreeId()).orElseThrow(DegreeNotFoundException::new);
        university = universityService.addDegreeToUniversity(university, degree);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @DeleteMapping("/{id}/degrees/{degreeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto removeUniversityDegree(@PathVariable UUID id,
            @PathVariable UUID degreeId) {
        University university = universityService.getUniversityById(id).orElseThrow(UniversityNotFoundException::new);
        Degree degree = degreeService.getDegreeById(degreeId).orElseThrow(DegreeNotFoundException::new);

        university = universityService.removeDegreeFromUniversity(university, degree);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    // TODO: soft deletion
    // @DeleteMapping("/{id}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // public void deleteUniversity(@PathVariable UUID id) {
    // University university =
    // universityService.getUniversityById(id).orElseThrow(UniversityNotFoundException::new);
    // universityService.deleteUniversity(university);
    // }

}
