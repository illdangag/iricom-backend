package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.data.request.AccountGroupInfoSearch;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 계정 그룹 - 조회")
@Transactional
public class AccountGroupServiceSearchTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    @Autowired
    public AccountGroupServiceSearchTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("목록 조회")
    public void getList() throws Exception {
        List<TestAccountGroupInfo> accountGroupList = setRandomAccountGroup(10);
        List<String> accountGroupIdList = accountGroupList.stream()
                .map(TestAccountGroupInfo::getId)
                .collect(Collectors.toList());

        List<String> list = this.getAllList(AccountGroupInfoSearch.builder().build(), (searchRequest) -> {
            AccountGroupInfoList accountGroupInfoList = accountGroupService.getAccountGroupInfoList((AccountGroupInfoSearch) searchRequest);
            return accountGroupInfoList.getAccountGroupInfoList().stream()
                    .map(AccountGroupInfo::getId)
                    .collect(Collectors.toList());
        });

        accountGroupIdList.forEach(id -> {
            Assertions.assertTrue(list.contains(id));
        });
    }
}
