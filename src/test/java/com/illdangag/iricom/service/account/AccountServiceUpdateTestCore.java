package com.illdangag.iricom.service.account;

import com.illdangag.iricom.core.data.request.AccountInfoUpdate;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.AccountService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.UUID;

@DisplayName("service: 계정 - 수정")
@Transactional
public class AccountServiceUpdateTestCore extends IricomTestServiceSuite {
    @Autowired
    private AccountService accountService;

    public AccountServiceUpdateTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("닉네임 수정")
    void updateAccountNickname() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        String nickname = UUID.randomUUID().toString().substring(0, 20);
        AccountInfoUpdate accountInfoUpdate = AccountInfoUpdate.builder()
                .nickname(nickname)
                .build();

        accountService.updateAccountDetail(account.getId(), accountInfoUpdate);
    }

    @Test
    @DisplayName("닉네임을 빈문자열로 수정")
    void updateAccountEmptyNickname() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        AccountInfoUpdate accountInfoUpdate = AccountInfoUpdate.builder()
                .nickname("")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> accountService.updateAccountDetail(account.getId(), accountInfoUpdate));
        Assertions.assertEquals("02000002", exception.getErrorCode());
        Assertions.assertEquals("Missing required fields: nickname.", exception.getMessage());
    }
}
