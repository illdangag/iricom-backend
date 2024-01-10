package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.data.request.AccountGroupInfoSearch;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 계정 그룹 - 조회")
@Slf4j
@Transactional
public class AccountGroupServiceSearchTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 계정 그룹
    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description")
            .accountList(Arrays.asList(common00, common01)).boardList(Arrays.asList(testBoardInfo00, testBoardInfo01)).build();

    @Autowired
    public AccountGroupServiceSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);
        addTestAccountGroupInfo(testAccountGroupInfo00);

        init();
    }

    @Test
    @DisplayName("목록 조회")
    public void getList() throws Exception {
        String accountGroupId = String.valueOf(getAccountGroup(testAccountGroupInfo00).getId());

        List<String> list = getAllList(AccountGroupInfoSearch.builder().build(), (searchRequest) -> {
            AccountGroupInfoList accountGroupInfoList = accountGroupService.getAccountGroupInfoList((AccountGroupInfoSearch) searchRequest);
            return accountGroupInfoList.getAccountGroupInfoList().stream()
                    .map(AccountGroupInfo::getId)
                    .collect(Collectors.toList());
        });

        Assertions.assertTrue(list.contains(accountGroupId));
    }
}
