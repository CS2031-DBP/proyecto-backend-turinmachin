package com.turinmachin.unilife.university.domain;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.common.exception.UnsupportedMediaTypeException;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.degree.domain.DegreeService;
import com.turinmachin.unilife.degree.exception.DegreeNotFoundException;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.fileinfo.domain.FileInfoService;
import com.turinmachin.unilife.university.dto.CreateUniversityDto;
import com.turinmachin.unilife.university.dto.DegreeAlreadyPresent;
import com.turinmachin.unilife.university.dto.UpdateUniversityDto;
import com.turinmachin.unilife.university.exception.UniversityNameConflictException;
import com.turinmachin.unilife.university.exception.UniversityShortNameConflictException;
import com.turinmachin.unilife.university.infrastructure.UniversityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    private final DegreeService degreeService;

    private final FileInfoService fileInfoService;

    private final ModelMapper modelMapper;

    public List<University> getAllActiveUniversities() {
        return universityRepository.findByActiveTrue();
    }

    public Optional<University> getActiveUniversityById(UUID id) {
        return universityRepository.findByIdAndActiveTrue(id);
    }

    public Optional<University> getActiveUniversityByEmailDomain(String emailDomain) {
        return universityRepository.findByEmailDomainsContaining(emailDomain);
    }

    public University createUniversity(CreateUniversityDto dto) {
        if (universityRepository.existsByName(dto.getName())) {
            throw new UniversityNameConflictException();
        }

        if (universityRepository.existsByShortName(dto.getShortName())) {
            throw new UniversityShortNameConflictException();
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
        if (universityRepository.existsByNameAndIdNot(dto.getName(), university.getId())) {
            throw new UniversityNameConflictException();
        }

        if (universityRepository.existsByShortNameAndIdNot(dto.getShortName(), university.getId())) {
            throw new UniversityShortNameConflictException();
        }

        university.setName(dto.getName());
        university.setShortName(dto.getShortName());
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

    public University updateUniversityPicture(University university, MultipartFile file) throws IOException {
        if (!fileInfoService.isContentTypeImage(file.getContentType())) {
            throw new UnsupportedMediaTypeException();
        }

        FileInfo oldPicture = university.getPicture();
        FileInfo newPicture = fileInfoService.createFile(file);

        university.setPicture(newPicture);
        university = universityRepository.save(university);

        if (oldPicture != null) {
            fileInfoService.deleteFile(oldPicture);
        }
        return university;
    }

    public University deleteUniversityPicture(University university) {
        FileInfo oldPicture = university.getPicture();

        if (oldPicture == null) {
            throw new ConflictException("University does not have a picture");
        }

        university.setPicture(null);
        university = universityRepository.save(university);
        fileInfoService.deleteFile(oldPicture);
        return university;
    }

}
