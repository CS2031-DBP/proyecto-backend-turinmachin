package com.turinmachin.unilife.university.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.domain.DegreeService;
import com.turinmachin.unilife.degree.exception.DegreeNotFoundException;
import com.turinmachin.unilife.university.dto.CreateUniversityDto;
import com.turinmachin.unilife.university.dto.DegreeAlreadyPresent;
import com.turinmachin.unilife.university.dto.UpdateUniversityDto;
import com.turinmachin.unilife.university.exception.UniversityNameConflictException;
import com.turinmachin.unilife.university.infrastructure.UniversityRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    private final DegreeService degreeService;

    private final ModelMapper modelMapper;

    public List<University> getAllActiveUniversities() {
        return universityRepository.findByActiveTrue();
    }

    public Optional<University> getActiveUniversityById(UUID id) {
        return universityRepository.findByIdAndActiveTrue(id);
    }

    public University createUniversity(CreateUniversityDto dto) {
        if (universityRepository.existsByName(dto.getName())) {
            throw new UniversityNameConflictException();
        }

        University university = modelMapper.map(dto, University.class);

        List<Degree> degrees = dto.getDegreeIds()
                .stream()
                .map(id -> degreeService.getDegreeById(id).orElseThrow(DegreeNotFoundException::new))
                .toList();
        university.setDegrees(degrees);

        return universityRepository.save(university);

    }

    public University updateUniversity(University university, UpdateUniversityDto dto) {
        if (universityRepository.existsByName(dto.getName())) {
            throw new UniversityNameConflictException();
        }

        university.setName(dto.getName());
        university.setEmailDomains(dto.getEmailDomains());

        return universityRepository.save(university);
    }

    public University addDegreeToUniversity(University university, Degree degree) {
        if (university.getDegrees().contains(degree)) {
            throw new DegreeAlreadyPresent();
        }
        university.getDegrees().add(degree);
        return universityRepository.save(university);
    }

    public University removeDegreeFromUniversity(University university, Degree degree) {
        university.getDegrees().remove(degree);
        return universityRepository.save(university);
    }

    public University deactivateUniversity(University university) {
        university.setActive(false);
        return universityRepository.save(university);
    }

}
