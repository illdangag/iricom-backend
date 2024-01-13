package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.server.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.server.data.response.PersonalMessageInfo;
import com.illdangag.iricom.server.data.response.PersonalMessageInfoList;

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
     * 개인 쪽지 목록 조회
     */
    PersonalMessageInfoList getPersonalMessageInfoList(String accountId, @Valid PersonalMessageInfoSearch personalMessageInfoSearch);

    PersonalMessageInfoList getPersonalMessageInfoList(Account account, @Valid PersonalMessageInfoSearch personalMessageInfoSearch);
}
