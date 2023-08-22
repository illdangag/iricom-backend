package com.illdangag.iricom.storage.s3.service.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.exception.IricomException;
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

@Slf4j
@Service
public class S3StorageServiceImpl implements StorageService {

    private final FileRepository fileRepository;
    private final String ENDPOINT;
    private final Regions REGIONS;
    private final String ACCESS_KEY;
    private final String SECRET_KEY;
    private final String BUCKET;

    @Autowired
    public S3StorageServiceImpl(FileRepository fileRepository,
                                @Value("${storage.s3.endpoint:#{null}}") String endpoint,
                                @Value("${storage.s3.region:#{null}}") String region,
                                @Value("${storage.s3.accessKey:#{null}}") String accessKey,
                                @Value("${storage.s3.secretKey:#{null}}") String secretKey,
                                @Value("${storage.s3.bucket:#{null}}") String bucket) {
        this.fileRepository = fileRepository;
        this.ENDPOINT = endpoint;
        this.REGIONS = Regions.fromName(region);
        this.ACCESS_KEY = accessKey;
        this.SECRET_KEY = secretKey;
        this.BUCKET = bucket;
    }

    @Override
    public FileMetadataInfo uploadFile(Account account, String fileName, InputStream inputStream) {
        int fileSize = 0;

        try {
            fileSize = inputStream.available();
        } catch (Exception exception) {
            throw new IricomException(IricomS3StorageErrorCode.INVALID_UPLOAD_FILE);
        }

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(account)
                .name(fileName)
                .size((long) fileSize)
                .build();

        this.fileRepository.saveFileMetadata(fileMetadata);

        String path = this.getPath(fileMetadata);

        AmazonS3 amazonS3 = this.getAmazonS3();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.BUCKET, path, inputStream, metadata);
        amazonS3.putObject(putObjectRequest);

        return new FileMetadataInfo(fileMetadata);
    }

    @Override
    public InputStream downloadFile(String id) {
        return null;
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
}
