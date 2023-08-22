package com.illdangag.iricom.storage.service.s3;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.s3.service.impl.S3StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

@Slf4j
public class S3StorageServiceTest extends IricomTestSuite {
    private final S3StorageServiceImpl s3StorageServiceImpl;

    private final String IMAGE_FILE_NAME = "spring_boot_icon.png";

    @Autowired
    public S3StorageServiceTest(ApplicationContext context, S3StorageServiceImpl s3StorageServiceImpl) {
        super(context);
        this.s3StorageServiceImpl = s3StorageServiceImpl;
    }

    @Test
    public void uploadFileTest() {
        Account account = getAccount(common00);
        InputStream sampleImageInputStream = this.getSampleImageInputStream();

        FileMetadataInfo fileMetadataInfo = this.s3StorageServiceImpl.uploadFile(account, IMAGE_FILE_NAME, sampleImageInputStream);
    }

    private InputStream getSampleImageInputStream() {
        return S3StorageServiceTest.class.getClassLoader().getResourceAsStream(IMAGE_FILE_NAME);
    }
}
