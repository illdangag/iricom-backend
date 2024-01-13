package com.illdangag.iricom.server.data.response;

import com.illdangag.iricom.server.data.entity.PersonalMessage;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class PersonalMessageInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String title;

    private String message;

    private Boolean receivedConfirm;

    public PersonalMessageInfo(PersonalMessage personalMessage) {
        this.id = String.valueOf(personalMessage.getId());
        this.createDate = DateTimeUtils.getLong(personalMessage.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(personalMessage.getUpdateDate());
        this.title = personalMessage.getTitle();
        this.message = personalMessage.getMessage();
        this.receivedConfirm = personalMessage.getReceivedConfirm();
    }
}
