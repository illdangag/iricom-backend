package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.AccountInfoSearch;
import com.illdangag.iricom.server.data.request.AccountInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.AccountInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 계정 목록 조회
     */
    @ApiCallLog(apiCode = "AC_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<AccountInfoList> getAccountInfoList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                              @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                              @RequestParam(name = "keyword", defaultValue = "", required = false) String keyword) {
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid");
        }

        AccountInfoSearch searchOption = AccountInfoSearch.builder()
                .skip(skip)
                .limit(limit)
                .keyword(keyword)
                .build();

        AccountInfoList accountInfoList = this.accountService.getAccountInfoList(searchOption);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfoList);
    }

    /**
     * 계정 정보 조회
     */
    @ApiCallLog(apiCode = "AC_002")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "/{account_id}")
    public ResponseEntity<AccountInfo> getAccountInfo(@PathVariable(value = "account_id") String accountId) {
        AccountInfo accountInfo = this.accountService.getAccountInfo(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }

    /**
     * 계정 정보 수정
     * 시스템 관리자가 다른 계정 정보를 수정
     */
    @ApiCallLog(apiCode = "AC_003")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.PATCH, value = "/{account_id}")
    public ResponseEntity<AccountInfo> updateAccountInfo(@PathVariable(value = "account_id") String accountId,
                                                         @RequestBody @Validated AccountInfoUpdate accountInfoUpdate) {
        AccountInfo accountInfo = this.accountService.updateAccountDetail(accountId, accountInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }

    /**
     * 계정 정보 수정
     * 자신의 계정 정보 수정
     */
    @ApiCallLog(apiCode = "AC_004")
    @Auth(role = AuthRole.UNREGISTERED_ACCOUNT)
    @RequestMapping(method = RequestMethod.PATCH, value = "")
    public ResponseEntity<AccountInfo> updateAccountInfo(@RequestContext Account account,
                                                         @RequestBody @Validated AccountInfoUpdate accountInfoUpdate) {
        AccountInfo accountInfo = this.accountService.updateAccountDetail(account, accountInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }
}
