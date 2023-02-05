package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountDetail;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInfo {
    private String id;

    private String email;

    private long createDate;

    private long lastActivityDate;

    private String nickname;

    private String description;

    public AccountInfo(Account account, AccountDetail accountDetail) {
        this.id = account.getId().toString();
        this.email = account.getEmail();
        this.createDate = DateTimeUtils.getLong(account.getCreateDate());
        this.lastActivityDate = DateTimeUtils.getLong(account.getLastActivityDate());
        this.nickname = accountDetail.getNickname();
        this.description = accountDetail.getDescription();
    }
}
