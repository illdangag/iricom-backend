package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.AccountInfoSearch;
import com.illdangag.iricom.server.data.response.AccountInfoList;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
public class AccountServiceTest extends IricomTestSuite {
    @Autowired
    private AccountService accountService;

    @Autowired
    public AccountServiceTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("계정 검색")
    class SearchTest {
        @Test
        @DisplayName("상세 정보가 등록된 계정을 이메일로 조회")
        public void searchRegisteredAccount() throws Exception {
            AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                    .keyword("common00@iricom.com")
                    .build();

            AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
            long total = accountInfoList.getTotal();
            Assertions.assertEquals(1, total);
        }

        @Test
        @DisplayName("상세 정보가 등록된 계정을 닉네임으로 조회")
        public void searchRegisteredAccountByNickname() throws Exception {
            AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                    .keyword(common02.getNickname())
                    .build();

            AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
            long total = accountInfoList.getTotal();
            Assertions.assertEquals(1, total);
        }

        @Test
        @DisplayName("상세 정보가 등록되지 않은 계정을 이메일로 조회")
        public void searchUnregisteredAccount() throws Exception {
            Account account = getAccount(unknown00);

            Assertions.assertNotNull(account);
            AccountInfoSearch accountInfoSearch = AccountInfoSearch.builder()
                    .keyword(account.getEmail())
                    .build();

            AccountInfoList accountInfoList = accountService.getAccountInfoList(accountInfoSearch);
            long total = accountInfoList.getTotal();
            Assertions.assertEquals(1, total);
        }
    }
}
