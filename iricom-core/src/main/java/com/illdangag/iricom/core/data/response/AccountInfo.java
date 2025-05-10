package com.illdangag.iricom.core.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.AccountDetail;
import com.illdangag.iricom.core.data.entity.AccountPoint;
import com.illdangag.iricom.core.util.DateTimeUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInfo {
    private String id;

    private String email;

    private Long createDate;

    private Long lastActivityDate;

    private String nickname;

    private String description;

    private String auth;

    private Long point;

    public AccountInfo(Account account) {
        this.id = account.getId().toString();
        this.email = account.getEmail();
        this.createDate = DateTimeUtils.getLong(account.getCreateDate());
        this.lastActivityDate = DateTimeUtils.getLong(account.getLastActivityDate());

        AccountDetail accountDetail;
        if (account.getAccountDetail() == null) {
            accountDetail = AccountDetail.builder().build();
        } else {
            accountDetail = account.getAccountDetail();
        }
        this.nickname = accountDetail.getNickname();
        this.description = accountDetail.getDescription();
        this.auth = account.getAuth().getText();
        this.point = 0L;
        List<AccountPoint> accountPointList = account.getPointList();
        for (AccountPoint accountPoint : accountPointList) {
            this.point += accountPoint.getPoint();
        }
    }
}
