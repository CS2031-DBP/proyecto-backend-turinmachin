package com.turinmachin.unilife.image.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turinmachin.unilife.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    void deleteByKey(String key);

}
