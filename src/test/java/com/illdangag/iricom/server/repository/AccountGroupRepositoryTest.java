package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@DisplayName("AccountGroupRepository")
public class AccountGroupRepositoryTest extends IricomTestSuite {
    TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    @Autowired
    AccountGroupRepository accountGroupRepository;

    @Autowired
    public AccountGroupRepositoryTest(ApplicationContext context) {
        super(context);

        super.setBoard(Arrays.asList(testBoardInfo00));
    }

    @Test
    @DisplayName("계정 그룹 생성")
    public void createAccountGroup() throws Exception {

    }
}
