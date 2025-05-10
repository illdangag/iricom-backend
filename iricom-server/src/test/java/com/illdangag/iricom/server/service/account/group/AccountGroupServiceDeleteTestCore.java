package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.AccountGroupService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
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
public class AccountGroupServiceDeleteTestCore extends IricomTestServiceSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    @Autowired
    public AccountGroupServiceDeleteTestCore(ApplicationContext context) {
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
