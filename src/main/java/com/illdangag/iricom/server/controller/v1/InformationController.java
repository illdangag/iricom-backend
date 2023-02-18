package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.MyInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/v1/infos")
public class InformationController {

    @Autowired
    public InformationController() {
    }

    @ApiCallLog(apiCode = "IF_001")
    @Auth(role = AuthRole.UNREGISTERED_ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<MyInformation> getMyAccountInfo(@RequestContext Account account) {
        AccountInfo accountInfo = new AccountInfo(account);

        MyInformation myInformation = MyInformation.builder()
                .account(accountInfo)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(myInformation);
    }
}
