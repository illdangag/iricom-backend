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

import javax.transaction.Transactional;

@DisplayName("service: 계정 그룹 - 삭제")
@Slf4j
@Transactional
public class AccountGroupServiceDeleteTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    @Autowired
    public AccountGroupServiceDeleteTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("계정 그룹 삭제")
    public void delete() throws Exception {
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = TestAccountGroupInfo.builder()
                .title("testAccountGroupInfo00")
                .description("description")
                .build();
        this.setAccountGroup(accountGroup);

        // 삭제 전에 계정 그룹 조회 시도
        Assertions.assertDoesNotThrow(() -> {
            accountGroupService.getAccountGroupInfo(accountGroup.getId());
        });

        // 계정 그룹 삭제
        accountGroupService.deleteAccountGroupInfo(accountGroup.getId());

        // 삭제한 계정 그룹으로 조회 시도
        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.getAccountGroupInfo(accountGroup.getId());
        });
    }
}
