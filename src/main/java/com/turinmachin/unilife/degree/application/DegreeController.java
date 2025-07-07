package com.turinmachin.unilife.degree.application;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.domain.DegreeService;
import com.turinmachin.unilife.degree.dto.CreateDegreeDto;
import com.turinmachin.unilife.degree.dto.DegreeResponseDto;
import com.turinmachin.unilife.degree.dto.DegreeWithStatsDto;
import com.turinmachin.unilife.degree.dto.UpdateDegreeDto;
import com.turinmachin.unilife.degree.exception.DegreeNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/degrees")
@RequiredArgsConstructor
public class DegreeController {

    private final DegreeService degreeService;
    private final ModelMapper modelMapper;

    @GetMapping
    public Page<DegreeResponseDto> getPaginatedDegrees(
            @RequestParam(required = false) UUID universityId,
            @RequestParam(required = false) String query,
            Pageable pageable) {
        Page<Degree> degrees;

        if (query != null) {
            degrees = degreeService.searchDegrees(query, universityId, pageable);
        } else if (universityId != null) {
            degrees = degreeService.getDegreesByUniversityId(universityId, pageable);
        } else {
            degrees = degreeService.getAllDegrees(pageable);
        }

        return degrees.map(degree -> modelMapper.map(degree, DegreeResponseDto.class));
    }

    @GetMapping("/all")
    public List<DegreeResponseDto> getAllDegrees(@RequestParam(required = false) UUID universityId) {
        List<Degree> degrees;
        if (universityId != null) {
            degrees = degreeService.getDegreesByUniversityId(universityId);
        } else {
            degrees = degreeService.getAllDegrees();
        }

        return degrees.stream().map(degree -> modelMapper.map(degree, DegreeResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public DegreeWithStatsDto getDegree(@PathVariable UUID id) {
        return degreeService.getDegreeWithStatsById(id).orElseThrow(DegreeNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DegreeResponseDto createDegree(@Valid @RequestBody CreateDegreeDto dto) {
        Degree degree = degreeService.createDegree(dto);
        return modelMapper.map(degree, DegreeResponseDto.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DegreeResponseDto updateDegree(@PathVariable UUID id, @Valid @RequestBody UpdateDegreeDto dto) {
        Degree degree = degreeService.getDegreeById(id).orElseThrow(DegreeNotFoundException::new);
        Degree updatedDegree = degreeService.updateDegree(degree, dto);
        return modelMapper.map(updatedDegree, DegreeResponseDto.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteDegree(@PathVariable UUID id) {
        Degree degree = degreeService.getDegreeById(id).orElseThrow(DegreeNotFoundException::new);
        degreeService.deleteDegree(degree);
    }

}
