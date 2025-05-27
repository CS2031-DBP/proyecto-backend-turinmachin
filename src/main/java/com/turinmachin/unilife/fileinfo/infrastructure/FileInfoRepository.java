package com.turinmachin.unilife.fileinfo.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turinmachin.unilife.fileinfo.domain.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo, UUID> {

    void deleteByKey(String key);

}
