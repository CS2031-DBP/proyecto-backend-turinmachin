package com.turinmachin.unilife.degree.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.turinmachin.unilife.degree.dto.CreateDegreeDto;
import com.turinmachin.unilife.degree.dto.UpdateDegreeDto;
import com.turinmachin.unilife.degree.exception.DegreeNameConflictException;
import com.turinmachin.unilife.degree.infrastructure.DegreeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DegreeService {

    private final DegreeRepository degreeRepository;

    private final ModelMapper modelMapper;

    public List<Degree> getAllDegrees() {
        return degreeRepository.findAll();
    }

    public Optional<Degree> getDegreeById(UUID id) {
        return degreeRepository.findById(id);
    }

    public Degree createDegree(CreateDegreeDto dto) {
        if (degreeRepository.existsByName(dto.getName())) {
            throw new DegreeNameConflictException();
        }

        Degree degree = modelMapper.map(dto, Degree.class);
        return degreeRepository.save(degree);
    }

    public Degree updateDegree(Degree degree, UpdateDegreeDto dto) {
        if (degreeRepository.existsByName(dto.getName())) {
            throw new DegreeNameConflictException();
        }

        degree.setName(dto.getName());
        return degreeRepository.save(degree);
    }

    public void deleteDegree(Degree degree) {
        degreeRepository.delete(degree);
    }

}
