package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.illdangag.iricom.server.data.entity.PersonalMessage;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class PersonalMessageInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String title;

    @JsonInclude(Include.NON_NULL)
    private String message;

    private Boolean receivedConfirm;

    private AccountInfo sendAccount;

    private AccountInfo receiveAccount;

    public PersonalMessageInfo(PersonalMessage personalMessage, boolean includeMessage) {
        this.id = String.valueOf(personalMessage.getId());
        this.createDate = DateTimeUtils.getLong(personalMessage.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(personalMessage.getUpdateDate());
        this.title = personalMessage.getTitle();
        this.receivedConfirm = personalMessage.getReceivedConfirm();
        if (includeMessage) {
            this.message = personalMessage.getMessage();
        }
        this.sendAccount = new AccountInfo(personalMessage.getSendAccount());
        this.receiveAccount = new AccountInfo(personalMessage.getReceiveAccount());
    }
}
