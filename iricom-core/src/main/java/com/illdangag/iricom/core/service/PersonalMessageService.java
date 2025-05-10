package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.core.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.core.data.response.PersonalMessageInfo;
import com.illdangag.iricom.core.data.response.PersonalMessageInfoList;

import javax.validation.Valid;

public interface PersonalMessageService {
    /**
     * 개인 쪽지 발송
     */
    PersonalMessageInfo createPersonalMessageInfo(String accountId, @Valid PersonalMessageInfoCreate personalMessageInfoCreate);

    PersonalMessageInfo createPersonalMessageInfo(Account account, @Valid PersonalMessageInfoCreate personalMessageInfoCreate);

    /**
     * 개인 쪽지 조회
     */

    PersonalMessageInfo getPersonalMessageInfo(String accountId, String personalMessageId);

    PersonalMessageInfo getPersonalMessageInfo(Account account, String personalMessageId);

    /**
     * 수신 개인 쪽지 조회
     */
    PersonalMessageInfo getReceivePersonalMessageInfo(String accountId, String personalMessageId);

    PersonalMessageInfo getReceivePersonalMessageInfo(Account account, String personalMessageId);

    /**
     * 수신 개인 쪽지 목록 조회
     */
    PersonalMessageInfoList getReceivePersonalMessageInfoList(String accountId, @Valid PersonalMessageInfoSearch personalMessageInfoSearch);

    PersonalMessageInfoList getReceivePersonalMessageInfoList(Account account, @Valid PersonalMessageInfoSearch personalMessageInfoSearch);

    /**
     * 송신 개인 쪽지 목록 조회
     */

    PersonalMessageInfo getSendPersonalMessageInfo(String accountId, String personalMessageId);

    PersonalMessageInfo getSendPersonalMessageInfo(Account account, String personalMessageId);

    /**
     * 송신 개인 쪽지 목록 조회
     */
    PersonalMessageInfoList getSendPersonalMessageInfoList(String accountId, @Valid PersonalMessageInfoSearch personalMessageInfoSearch);

    PersonalMessageInfoList getSendPersonalMessageInfoList(Account account, @Valid PersonalMessageInfoSearch personalMessageInfoSearch);

    /**
     * 수신 개인 쪽지 삭제
     */
    PersonalMessageInfo deletePersonalMessageInfo(String accountId, String personalMessageId);

    PersonalMessageInfo deletePersonalMessageInfo(Account account, String personalMessageId);
}
