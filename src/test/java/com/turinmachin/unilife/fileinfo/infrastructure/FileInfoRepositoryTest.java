package com.turinmachin.unilife.fileinfo.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.turinmachin.unilife.PostgresContainerConfig;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;

@DataJpaTest
@Testcontainers
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FileInfoRepositoryTest {

    @Autowired
    private FileInfoRepository fileInfoRepository;

    private FileInfo fileInfo1;
    private FileInfo fileInfo2;
    private FileInfo fileInfo3;

    @BeforeEach
    private void setup() {
        fileInfo1 = new FileInfo();
        fileInfo1.setMediaType("image/png");
        fileInfo1.setKey("abc");
        fileInfo1.setUrl("https://coolcdn.com/hello.png");
        fileInfo1 = fileInfoRepository.save(fileInfo1);

        fileInfo2 = new FileInfo();
        fileInfo2.setMediaType("image/jpeg");
        fileInfo2.setKey("def");
        fileInfo2.setUrl("https://coolcdn.com/goodbye.jpeg");
        fileInfo2 = fileInfoRepository.save(fileInfo2);

        fileInfo3 = new FileInfo();
        fileInfo3.setMediaType("video/mp4");
        fileInfo3.setKey("000");
        fileInfo3.setUrl("https://coolcdn.com/good_afternoong.mp4");
        fileInfo3 = fileInfoRepository.save(fileInfo3);
    }

    @Test
    public void testDeleteByKey() {
        Assertions.assertTrue(fileInfoRepository.existsById(fileInfo1.getId()));
        Assertions.assertTrue(fileInfoRepository.existsById(fileInfo2.getId()));
        Assertions.assertTrue(fileInfoRepository.existsById(fileInfo3.getId()));

        fileInfoRepository.deleteByKey(fileInfo1.getKey());
        Assertions.assertFalse(fileInfoRepository.existsById(fileInfo1.getId()));
        Assertions.assertTrue(fileInfoRepository.existsById(fileInfo2.getId()));
        Assertions.assertTrue(fileInfoRepository.existsById(fileInfo3.getId()));

        fileInfoRepository.deleteByKey(fileInfo3.getKey());
        Assertions.assertFalse(fileInfoRepository.existsById(fileInfo1.getId()));
        Assertions.assertTrue(fileInfoRepository.existsById(fileInfo2.getId()));
        Assertions.assertFalse(fileInfoRepository.existsById(fileInfo3.getId()));

        fileInfoRepository.deleteByKey(fileInfo2.getKey());
        Assertions.assertFalse(fileInfoRepository.existsById(fileInfo1.getId()));
        Assertions.assertFalse(fileInfoRepository.existsById(fileInfo2.getId()));
        Assertions.assertFalse(fileInfoRepository.existsById(fileInfo3.getId()));
    }

}
