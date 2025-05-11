package com.illdangag.iricom.storage.s3.service.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.type.AccountAuth;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.repository.AccountRepository;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.repository.FileRepository;
import com.illdangag.iricom.storage.s3.exception.IricomS3StorageErrorCode;
import com.illdangag.iricom.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class S3StorageServiceImpl implements StorageService {
    private final AccountRepository accountRepository;
    private final FileRepository fileRepository;
    private final String ENDPOINT;
    private final Regions REGIONS;
    private final String ACCESS_KEY;
    private final String SECRET_KEY;
    private final String BUCKET;

    @Autowired
    public S3StorageServiceImpl(AccountRepository accountRepository, FileRepository fileRepository,
                                @Value("${storage.s3.endpoint:#{null}}") String endpoint,
                                @Value("${storage.s3.region:#{null}}") String region,
                                @Value("${storage.s3.accessKey:#{null}}") String accessKey,
                                @Value("${storage.s3.secretKey:#{null}}") String secretKey,
                                @Value("${storage.s3.bucket:#{null}}") String bucket) {
        this.accountRepository = accountRepository;
        this.fileRepository = fileRepository;
        this.ENDPOINT = endpoint;
        this.REGIONS = Regions.fromName(region);
        this.ACCESS_KEY = accessKey;
        this.SECRET_KEY = secretKey;
        this.BUCKET = bucket;

        log.info("storage: S3");
        log.info("- endpoint: {}", endpoint);
        log.info("- region: {}", region);
        log.info("- accessKey: {}", accessKey);
        log.info("- secretKey: {}", secretKey);
        log.info("- bucket: {}", bucket);
    }

    @Override
    public FileMetadataInfo uploadFile(String accountId, String fileName, String contentType, InputStream inputStream) {
        Account account = this.getAccount(accountId);
        return this.uploadFile(account, fileName, contentType, inputStream);
    }

    @Override
    public FileMetadataInfo uploadFile(Account account, String fileName, String contentType, InputStream inputStream) {
        int fileSize = 0;

        try {
            fileSize = inputStream.available();
        } catch (Exception exception) {
            throw new IricomException(IricomS3StorageErrorCode.INVALID_UPLOAD_FILE);
        }

        String newFileName = this.createNewFileName(fileName);

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(account)
                .name(newFileName)
                .contentType(contentType)
                .size((long) fileSize)
                .build();

        this.fileRepository.saveFileMetadata(fileMetadata);

        String path = this.getPath(fileMetadata);
        AmazonS3 amazonS3 = this.getAmazonS3();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.BUCKET, path, inputStream, metadata);

        try {
            amazonS3.putObject(putObjectRequest);
        } catch (Exception exception) {
            throw new IricomException(IricomS3StorageErrorCode.FAIL_TO_SAVE_OBJECT_STORAGE);
        }

        return new FileMetadataInfo(fileMetadata);
    }

    @Override
    public IricomFileInputStream downloadFile(String id) {
        UUID fileMetadataId = null;

        try {
            fileMetadataId = UUID.fromString(id);
        } catch (Exception exception) {
            throw new IricomException(IricomS3StorageErrorCode.NOT_EXIST_FILE);
        }

        Optional<FileMetadata> fileMetadataOptional = this.fileRepository.getFileMetadata(fileMetadataId);
        FileMetadata fileMetadata = fileMetadataOptional.orElseThrow(() -> new IricomException(IricomS3StorageErrorCode.NOT_EXIST_FILE));

        String filePath = this.getPath(fileMetadata);
        AmazonS3 amazonS3 = this.getAmazonS3();

        GetObjectRequest getObjectRequest = new GetObjectRequest(this.BUCKET, filePath);
        S3Object s3Object;

        try {
            s3Object = amazonS3.getObject(getObjectRequest);
        } catch (Exception exception) {
            throw new IricomException(IricomS3StorageErrorCode.NOT_EXIST_FILE);
        }

        return new IricomFileInputStream(s3Object.getObjectContent(), fileMetadata);
    }

    @Override
    public FileMetadataInfo deleteFile(String accountId, String fileId) {
        Account account = this.getAccount(accountId);
        return this.deleteFile(account, fileId);
    }

    @Override
    public FileMetadataInfo deleteFile(Account account, String fileId) {
        UUID fileMetadataId = null;

        try {
            fileMetadataId = UUID.fromString(fileId);
        } catch (Exception exception) {
            throw new IricomException(IricomS3StorageErrorCode.NOT_EXIST_FILE);
        }

        Optional<FileMetadata> fileMetadataOptional = this.fileRepository.getFileMetadata(fileMetadataId);
        FileMetadata fileMetadata = fileMetadataOptional.orElseThrow(() -> new IricomException(IricomS3StorageErrorCode.NOT_EXIST_FILE));

        Account fileOwner = fileMetadata.getAccount();
        if (account.getAuth() != AccountAuth.SYSTEM_ADMIN && !account.equals(fileOwner)) {
            // 시스템 관리자도 아니고 파일의 소유자가 아닌 경우 파일 삭제가 불가능
            throw new IricomException(IricomS3StorageErrorCode.INVALID_AUTHORIZATION_TO_DELETE_FILE);
        }

        String filePath = this.getPath(fileMetadata);
        AmazonS3 amazonS3 = this.getAmazonS3();
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.BUCKET, filePath);
        try {
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception exception) {
            log.error("Fail to delete file. id: {}, path: {}", fileId, filePath, exception);
        }

        fileMetadata.setDeleted(true);
        this.fileRepository.saveFileMetadata(fileMetadata);

        return new FileMetadataInfo(fileMetadata);
    }

    private AmazonS3 getAmazonS3() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);

        AmazonS3ClientBuilder awsClientBuilder = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.ACCESS_KEY, this.SECRET_KEY)))
                .withClientConfiguration(clientConfig);

        if (this.ENDPOINT != null) {
            awsClientBuilder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.ENDPOINT, this.REGIONS.getName()));
        } else {
            awsClientBuilder.withRegion(this.REGIONS);
        }

        return awsClientBuilder.build();
    }

    private String getPath(FileMetadata fileMetadata) {
        LocalDateTime createDate = fileMetadata.getCreateDate();
        return String.format("%04d-%02d-%02d/%s", createDate.getYear(), createDate.getMonthValue(), createDate.getDayOfMonth(), fileMetadata.getId());
    }

    private Account getAccount(String id) {
        long accountId = -1;
        try {
            accountId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
        Optional<Account> accountOptional = this.accountRepository.getAccount(accountId);
        return accountOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT));
    }
}
