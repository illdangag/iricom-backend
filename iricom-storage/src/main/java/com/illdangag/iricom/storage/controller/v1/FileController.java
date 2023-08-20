package com.illdangag.iricom.storage.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(value = "/v1/file")
public class FileController {

    @ApiCallLog(apiCode = "FL_TEST")
    @Auth(role = { AuthRole.NONE, })
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<String> requestTest() {

        return ResponseEntity.status(200).body("FL_001");
    }

    @ApiCallLog(apiCode = "FL_001")
    @Auth(role = { AuthRole.ACCOUNT, })
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile multipartFile) {

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @ApiCallLog(apiCode = "FL_002")
    @Auth(role = { AuthRole.NONE, })
    @RequestMapping(method = RequestMethod.GET, value = "/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable(value = "fileId") String fileId) {

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
