package com.illdangag.iricom.storage.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.data.entity.type.FileType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Slf4j
public class FileRepositoryTest extends IricomTestSuite {
    @Autowired
    private FileRepository fileRepository;

    public FileRepositoryTest(ApplicationContext context) {
        super(context);
    }

    @Test
    public void saveFileMetadata() {
        Account account = getAccount(common00);

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(account)
                .type(FileType.IMAGE)
                .path("/")
                .size(0L)
                .build();

        this.fileRepository.saveFileMetadata(fileMetadata);
    }
}
