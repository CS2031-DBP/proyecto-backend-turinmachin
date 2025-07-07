package com.turinmachin.unilife.university.application;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.domain.UniversityService;
import com.turinmachin.unilife.university.dto.CreateUniversityDto;
import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import com.turinmachin.unilife.university.dto.UniversityWithStatsDto;
import com.turinmachin.unilife.university.dto.UpdateUniversityDto;
import com.turinmachin.unilife.university.dto.UpdateUniversityPictureDto;
import com.turinmachin.unilife.university.exception.UniversityNotFoundException;
import com.turinmachin.unilife.user.domain.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public Page<UniversityResponseDto> getUniversities(@RequestParam(required = false) String name, Pageable pageable) {
        Page<University> universities;
        if (name == null) {
            universities = universityService.getAllUniversities(pageable);
        } else {
            universities = universityService.searchUniversities(name, pageable);
        }
        return universities.map(uni -> modelMapper.map(uni, UniversityResponseDto.class));
    }

    @GetMapping("/{id}")
    public UniversityResponseDto getUniversity(@PathVariable UUID id) {
        University university = universityService.getUniversityById(id).orElseThrow(UniversityNotFoundException::new);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @GetMapping("/with-stats/{id}")
    public UniversityWithStatsDto getUniversityWithStats(@PathVariable UUID id) {
        return universityService.getUniversityWithStatsById(id).orElseThrow(UniversityNotFoundException::new);
    }

    @GetMapping("/domain/{emailDomain}")
    public UniversityResponseDto getUniversityByEmailDomain(@PathVariable String emailDomain) {
        University university = universityService.getUniversityByEmailDomain(emailDomain)
                .orElseThrow(UniversityNotFoundException::new);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto createUniversity(@Valid @RequestBody CreateUniversityDto dto) {
        University university = universityService.createUniversity(dto);
        userService.syncUniversityAssociations(university);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto updateUniversity(@PathVariable UUID id, @Valid @RequestBody UpdateUniversityDto dto) {
        University university = universityService.getUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);
        university = universityService.updateUniversity(university, dto);
        userService.syncUniversityAssociations(university);
        userService.removeInvalidDegrees(university);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @PatchMapping("/{id}/picture")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto updateUniversityPicture(@PathVariable UUID id,
            @Valid @ModelAttribute UpdateUniversityPictureDto dto)
            throws IOException {
        University university = universityService.getUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);

        university = universityService.updateUniversityPicture(university, dto.getPicture());
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @DeleteMapping("/{id}/picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UniversityResponseDto deleteUserProfilePicture(@PathVariable UUID id) throws IOException {
        University university = universityService.getUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);

        university = universityService.deleteUniversityPicture(university);
        return modelMapper.map(university, UniversityResponseDto.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUniversity(@PathVariable UUID id) {
        University university = universityService.getUniversityById(id)
                .orElseThrow(UniversityNotFoundException::new);
        userService.detachUniversity(university);
        universityService.deactivateUniversity(university);
    }

}
