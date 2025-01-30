package com.illdangag.iricom.server.service.account.search;

import com.illdangag.iricom.server.data.request.AccountInfoSearch;
import com.illdangag.iricom.server.data.response.AccountInfoList;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;

@DisplayName("service: 계정 - 검색")
@Transactional
public class AccountServiceSearchTest extends IricomTestSuite {
    @Autowired
    private AccountService accountService;

    public AccountServiceSearchTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("상세 정보가 등록된 계정을 이메일로 조회")
    public void searchRegisteredAccount() throws Exception {
        TestAccountInfo testAccount = TestAccountInfo.builder()
                .email("accountSearchTest00@iricom.com")
                .nickname("accountSearchTest00")
                .description("this is accountSearchTest00.")
                .build();
        this.setAccount(testAccount);

        AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                .keyword("accountSearchTest00@iricom.com")
                .build();

        AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
        long total = accountInfoList.getTotal();
        Assertions.assertEquals(1, total);
    }

    @Test
    @DisplayName("상세 정보가 등록된 계정을 닉네임으로 조회")
    public void searchRegisteredAccountByNickname() throws Exception {
        TestAccountInfo testAccount = TestAccountInfo.builder()
                .email("accountSearchTest01@iricom.com")
                .nickname("accountSearchTest01")
                .description("this is accountSearchTest01.")
                .build();
        this.setAccount(testAccount);

        AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                .keyword("accountSearchTest01")
                .build();

        AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
        long total = accountInfoList.getTotal();
        Assertions.assertEquals(1, total);
    }

    @Test
    @DisplayName("상세 정보가 등록되지 않은 계정을 이메일로 조회")
    public void searchUnregisteredAccount() throws Exception {
        TestAccountInfo testAccount = TestAccountInfo.builder()
                .email("accountSearchTest02@iricom.com")
                .nickname("")
                .description("")
                .isUnregistered(true)
                .build();
        this.setAccount(testAccount);

        AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                .keyword("accountSearchTest02@iricom.com")
                .build();

        AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
        long total = accountInfoList.getTotal();
        Assertions.assertEquals(1, total);
    }
}
