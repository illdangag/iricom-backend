package com.illdangag.iricom.storage.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/v1/file")
public class FileController {

    @ApiCallLog(apiCode = "FL_001")
    @Auth(role = { AuthRole.NONE, })
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<String> requestTest() {

        return ResponseEntity.status(200).body("FL_001");
    }
}
