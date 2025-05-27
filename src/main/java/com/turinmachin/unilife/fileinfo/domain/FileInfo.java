package com.turinmachin.unilife.fileinfo.domain;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String mediaType;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        FileInfo fileInfo = (FileInfo) o;
        return Objects.equals(id, fileInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
