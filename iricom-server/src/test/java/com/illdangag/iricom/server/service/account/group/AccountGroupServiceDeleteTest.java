package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@DisplayName("service: 계정 그룹 - 삭제")
@Slf4j
public class AccountGroupServiceDeleteTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description").build();

    @Autowired
    public AccountGroupServiceDeleteTest(ApplicationContext context) {
        super(context);

        addTestAccountGroupInfo(testAccountGroupInfo00);
        init();
    }

    @Test
    @DisplayName("계정 그룹 삭제")
    public void delete() throws Exception {
        String accountGroupId = getAccountGroup(testAccountGroupInfo00).getId();

        accountGroupService.deleteAccountGroupInfo(accountGroupId);

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.getAccountGroupInfo(accountGroupId);
        });
    }
}
