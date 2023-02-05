package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
@DisplayName("AccountRepository 테스트")
public class AccountRepositoryTest extends IricomTestSuite {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    public AccountRepositoryTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("등록된 모든 계정의 수")
    public void testCase00() throws Exception {
        long totalAccountCount = accountRepository.getAccountCount();
        log.info("Total account count: {}", totalAccountCount);
    }
}
