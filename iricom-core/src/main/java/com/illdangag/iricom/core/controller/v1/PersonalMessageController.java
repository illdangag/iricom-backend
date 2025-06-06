package com.illdangag.iricom.core.controller.v1;

import com.illdangag.iricom.core.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.core.configuration.annotation.Auth;
import com.illdangag.iricom.core.configuration.annotation.AuthRole;
import com.illdangag.iricom.core.configuration.annotation.RequestContext;
import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.core.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.core.data.request.PersonalMessageStatus;
import com.illdangag.iricom.core.data.response.PersonalMessageInfo;
import com.illdangag.iricom.core.data.response.PersonalMessageInfoList;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.PersonalMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/personal/messages")
public class PersonalMessageController {
    private final PersonalMessageService personalMessageService;

    @Autowired
    public PersonalMessageController(PersonalMessageService personalMessageService) {
        this.personalMessageService = personalMessageService;
    }

    /**
     * 개인 쪽지 전송
     */
    @ApiCallLog(apiCode = "PM_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<PersonalMessageInfo> createPersonalMessage(@RequestBody @Valid PersonalMessageInfoCreate personalMessageInfoCreate,
                                                                     @RequestContext Account account) {
        PersonalMessageInfo personalMessageInfo = this.personalMessageService.createPersonalMessageInfo(account, personalMessageInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(personalMessageInfo);
    }


    /**
     * 수신 개인 쪽지 목록 조회
     */
    @ApiCallLog(apiCode = "PM_002")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "/receive")
    public ResponseEntity<PersonalMessageInfoList> getReceivePersonalMessageList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                                 @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                                 @RequestParam(name = "status", defaultValue = "all", required = false) PersonalMessageStatus status,
                                                                                 @RequestContext Account account) {
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .status(status)
                .skip(skip)
                .limit(limit)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(account, search);

        return ResponseEntity.status(HttpStatus.OK).body(personalMessageInfoList);
    }

    /**
     * 수신 개인 쪽지 조회
     */
    @ApiCallLog(apiCode = "PM_003")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "/receive/{personalMessageId}")
    public ResponseEntity<PersonalMessageInfo> getReceivePersonalMessage(@PathVariable(value = "personalMessageId") String personalMessageId,
                                                                         @RequestContext Account account) {
        PersonalMessageInfo personalMessageInfo = this.personalMessageService.getReceivePersonalMessageInfo(account, personalMessageId);
        return ResponseEntity.status(HttpStatus.OK).body(personalMessageInfo);
    }

    /**
     * 송신 개인 쪽지 목록 조회
     */
    @ApiCallLog(apiCode = "PM_004")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "/send")
    public ResponseEntity<PersonalMessageInfoList> getSendPersonalMessageList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                              @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                              @RequestParam(name = "type", defaultValue = "post", required = false) String typeVariable,
                                                                              @RequestContext Account account) {
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(skip)
                .limit(limit)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(account, search);

        return ResponseEntity.status(HttpStatus.OK).body(personalMessageInfoList);
    }

    /**
     * 송신 개인 쪽지 조회
     */
    @ApiCallLog(apiCode = "PM_005")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.GET, value = "/send/{personalMessageId}")
    public ResponseEntity<PersonalMessageInfo> getSendPersonalMessage(@PathVariable(value = "personalMessageId") String personalMessageId,
                                                                      @RequestContext Account account) {
        PersonalMessageInfo personalMessageInfo = this.personalMessageService.getSendPersonalMessageInfo(account, personalMessageId);
        return ResponseEntity.status(HttpStatus.OK).body(personalMessageInfo);
    }

    /**
     * 개인 쪽지 삭제
     */
    @ApiCallLog(apiCode = "PM_006")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/{personalMessageId}")
    public ResponseEntity<PersonalMessageInfo> deletePersonalMessage(@PathVariable(value = "personalMessageId") String personalMessageId,
                                                                     @RequestContext Account account) {
        PersonalMessageInfo personalMessageInfo = this.personalMessageService.deletePersonalMessageInfo(account, personalMessageId);
        return ResponseEntity.status(HttpStatus.OK).body(personalMessageInfo);
    }
}
