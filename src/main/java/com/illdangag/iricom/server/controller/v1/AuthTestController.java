package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/v1/api-test")
public class AuthTestController {

    @Autowired
    public AuthTestController() {
    }

    @Auth(role = AuthRole.NONE)
    @RequestMapping(value = "/none", method = RequestMethod.GET)
    public ResponseEntity<Object> testNone() {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{}");
    }

    @Auth(role = AuthRole.UNREGISTERED_ACCOUNT)
    @RequestMapping(value = "/unregistered-account", method = RequestMethod.GET)
    public ResponseEntity<Object> testUnregisteredAccount(@RequestContext Account account) {
        log.debug("Account: {}", account);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{}");
    }

    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public ResponseEntity<Object> testAccount() {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{}");
    }

    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(value = "/board-admin", method = RequestMethod.GET)
    public ResponseEntity<Object> testBoardAdmin(@RequestContext Account account, @RequestContext Board[] boards) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{}");
    }

    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(value = "/system-admin", method = RequestMethod.GET)
    public ResponseEntity<Object> testSystemAdmin(@RequestContext Account account, @RequestContext Board[] boards) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{}");
    }
}
