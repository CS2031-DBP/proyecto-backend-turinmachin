package com.turinmachin.unilife.degree.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.turinmachin.unilife.degree.dto.CreateDegreeDto;
import com.turinmachin.unilife.degree.dto.DegreeWithStatsDto;
import com.turinmachin.unilife.degree.dto.UpdateDegreeDto;
import com.turinmachin.unilife.degree.exception.DegreeNameConflictException;
import com.turinmachin.unilife.degree.exception.DegreeShortNameConflictException;
import com.turinmachin.unilife.degree.infrastructure.DegreeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DegreeService {

    private final DegreeRepository degreeRepository;

    private final ModelMapper modelMapper;

    public List<Degree> getAllDegrees() {
        return degreeRepository.findAllByOrderByName();
    }

    public Page<Degree> getAllDegrees(final Pageable pageable) {
        return degreeRepository.findAllByOrderByName(pageable);
    }

    public Page<Degree> searchDegrees(final String query, final UUID universityId, final Pageable pageable) {
        return degreeRepository.omnisearch(query, universityId, pageable);
    }

    public Page<Degree> getDegreesByUniversityId(final UUID universityId, final Pageable pageable) {
        return degreeRepository.findByUniversitiesIdOrderByName(universityId, pageable);
    }

    public List<Degree> getDegreesByUniversityId(final UUID universityId) {
        return degreeRepository.findByUniversitiesIdOrderByName(universityId);
    }

    public Optional<Degree> getDegreeById(final UUID id) {
        return degreeRepository.findById(id);
    }

    public Optional<DegreeWithStatsDto> getDegreeWithStatsById(final UUID id) {
        return degreeRepository.findWithStatsById(id);
    }

    public Degree createDegree(final CreateDegreeDto dto) {
        if (degreeRepository.existsByName(dto.getName())) {
            throw new DegreeNameConflictException();
        }

        if (dto.getShortName() != null && degreeRepository.existsByShortName(dto.getShortName())) {
            throw new DegreeShortNameConflictException();
        }

        final Degree degree = modelMapper.map(dto, Degree.class);
        return degreeRepository.save(degree);
    }

    public Degree updateDegree(final Degree degree, final UpdateDegreeDto dto) {
        if (degreeRepository.existsByNameAndIdNot(dto.getName(), degree.getId())) {
            throw new DegreeNameConflictException();
        }

        if (dto.getShortName() != null
                && degreeRepository.existsByShortNameAndIdNot(dto.getShortName(), degree.getId())) {
            throw new DegreeShortNameConflictException();
        }

        degree.setName(dto.getName());
        degree.setShortName(dto.getShortName());

        return degreeRepository.save(degree);
    }

    public void deleteDegree(final Degree degree) {
        degreeRepository.delete(degree);
    }

}
