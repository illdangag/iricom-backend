package com.illdangag.iricom.service.account;

import com.illdangag.iricom.core.data.request.AccountInfoSearch;
import com.illdangag.iricom.core.data.response.AccountInfoList;
import com.illdangag.iricom.core.service.AccountService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;

@DisplayName("service: 계정 - 검색")
@Transactional
public class AccountServiceSearchTestCore extends IricomTestServiceSuite {
    @Autowired
    private AccountService accountService;

    public AccountServiceSearchTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("상세 정보가 등록된 계정을 이메일로 조회")
    public void searchRegisteredAccount() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                .keyword(account.getEmail())
                .build();

        AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
        long total = accountInfoList.getTotal();
        Assertions.assertEquals(1, total);
    }

    @Test
    @DisplayName("상세 정보가 등록된 계정을 닉네임으로 조회")
    public void searchRegisteredAccountByNickname() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();

        AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                .keyword(account.getNickname())
                .build();

        AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
        long total = accountInfoList.getTotal();
        Assertions.assertEquals(1, total);
    }

    @Test
    @DisplayName("상세 정보가 등록되지 않은 계정을 이메일로 조회")
    public void searchUnregisteredAccount() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount(true);

        AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                .keyword(account.getEmail())
                .build();

        AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
        long total = accountInfoList.getTotal();
        Assertions.assertEquals(1, total);
    }
}
