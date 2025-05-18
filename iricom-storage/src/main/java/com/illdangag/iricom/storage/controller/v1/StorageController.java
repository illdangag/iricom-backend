package com.illdangag.iricom.storage.controller.v1;

import com.illdangag.iricom.core.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.core.configuration.annotation.Auth;
import com.illdangag.iricom.core.configuration.annotation.AuthRole;
import com.illdangag.iricom.core.configuration.annotation.RequestContext;
import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.storage.data.IricomFileInputStream;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;
import com.illdangag.iricom.storage.exception.IricomStorageErrorCode;
import com.illdangag.iricom.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping(value = "/v1/file")
public class StorageController {
    private StorageService storageService;

    @Autowired
    public StorageController(@Autowired(required = false) StorageService storageService) {
        this.storageService = storageService;
    }

    @ApiCallLog(apiCode = "FL_001")
    @Auth(role = { AuthRole.ACCOUNT, })
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<FileMetadataInfo> uploadFile(@RequestParam("file") MultipartFile multipartFile,
                                                       @RequestContext Account account) {
        String fileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        InputStream inputStream;

        try {
            inputStream = multipartFile.getInputStream();
        } catch (Exception exception) {
            throw new IricomException(IricomStorageErrorCode.INVALID_REQUEST_FILE_INPUT_STREAM);
        }

        FileMetadataInfo fileMetadataInfo = this.storageService.uploadFile(account, fileName, contentType, inputStream);
        return ResponseEntity.status(HttpStatus.OK).body(fileMetadataInfo);
    }


    @ApiCallLog(apiCode = "FL_002")
    @Auth(role = { AuthRole.NONE, })
    @RequestMapping(method = RequestMethod.GET, value = "/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable(value = "fileName") String fileName) {

        IricomFileInputStream inputStream = this.storageService.downloadFile(fileName);
        FileMetadataInfo fileMetadataInfo = inputStream.getFileMetadataInfo();
        String contentType = fileMetadataInfo.getContentType();

        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }
}
