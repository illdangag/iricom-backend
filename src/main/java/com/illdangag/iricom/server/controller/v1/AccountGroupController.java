package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.server.data.request.AccountGroupInfoSearch;
import com.illdangag.iricom.server.data.request.AccountGroupInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/group/account")
public class AccountGroupController {
    private final AccountGroupService accountGroupService;

    @Autowired
    public AccountGroupController(AccountGroupService accountGroupService) {
        this.accountGroupService = accountGroupService;
    }

    /**
     * 계정 그룹 생성
     */
    @ApiCallLog(apiCode = "AG_001")
    @Auth(role = { AuthRole.SYSTEM_ADMIN, })
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<AccountGroupInfo> createAccountGroupInfo(@RequestBody @Valid AccountGroupInfoCreate accountGroupInfoCreate) {
        AccountGroupInfo accountGroupInfo = this.accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(accountGroupInfo);
    }

    /**
     * 계정 그룹 목록 조회
     */
    @ApiCallLog(apiCode = "AG_002")
    @Auth(role = { AuthRole.SYSTEM_ADMIN, })
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<AccountGroupInfoList> getAccountGroupInfoList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                        @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable) {
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

        AccountGroupInfoSearch accountGroupInfoSearch = AccountGroupInfoSearch.builder()
                .skip(skip)
                .limit(limit)
                .build();

        AccountGroupInfoList accountGroupInfoList = this.accountGroupService.getAccountGroupInfoList(accountGroupInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(accountGroupInfoList);
    }


    /**
     * 계정 그룹 조회
     */
    @ApiCallLog(apiCode = "AG_003")
    @Auth(role = { AuthRole.SYSTEM_ADMIN, })
    @RequestMapping(method = RequestMethod.GET, value = "/{account_group_id}")
    public ResponseEntity<AccountGroupInfo> getAccountGroupInfo(@PathVariable(value = "account_group_id") String accountGroupId) {
        AccountGroupInfo accountGroupInfo = this.accountGroupService.getAccountGroupInfo(accountGroupId);
        return ResponseEntity.status(HttpStatus.OK).body(accountGroupInfo);
    }

    /**
     * 계정 그룹 수정
     */
    @ApiCallLog(apiCode =  "AG_004")
    @Auth(role = { AuthRole.SYSTEM_ADMIN, })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{account_group_id}")
    public ResponseEntity<AccountGroupInfo> updateAccountGroupInfo(@PathVariable(value = "account_group_id") String accountGroupId,
                                                                   @RequestBody @Valid AccountGroupInfoUpdate accountGroupInfoUpdate) {
        AccountGroupInfo accountGroupInfo = this.accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(accountGroupInfo);
    }

    /**
     * 계정 그룹 삭제
     */
    @ApiCallLog(apiCode = "AG_005")
    @Auth(role = { AuthRole.SYSTEM_ADMIN, })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{account_group_id}")
    public ResponseEntity<AccountGroupInfo> deleteAccountGroupInfo(@PathVariable(value = "account_group_id") String accountGroupId) {
        AccountGroupInfo accountGroupInfo = this.accountGroupService.deleteAccountGroupInfo(accountGroupId);
        return ResponseEntity.status(HttpStatus.OK).body(accountGroupInfo);
    }

}


