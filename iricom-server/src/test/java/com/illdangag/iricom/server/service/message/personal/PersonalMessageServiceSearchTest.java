package com.illdangag.iricom.server.service.message.personal;

import com.illdangag.iricom.server.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.server.data.response.PersonalMessageInfoList;
import com.illdangag.iricom.server.service.PersonalMessageService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;

@DisplayName("Service: 개인 쪽지 - 목록 조회")
@Transactional
public class PersonalMessageServiceSearchTest extends IricomTestSuite {
    @Autowired
    private PersonalMessageService personalMessageService;

    public PersonalMessageServiceSearchTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("발신 목록 조회")
    public void getSendPersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 20);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(sender.getId(), search);
        Assertions.assertEquals(20, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(20, personalMessageInfoList.getTotal());
    }

    @Test
    @DisplayName("발신 목록 조회 - skip")
    public void getSkipSendPersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(5)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(sender.getId(), search);
        Assertions.assertEquals(8, personalMessageInfoList.getTotal());
        Assertions.assertEquals(5, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(3, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("발신 목록 조회 - limit")
    public void getLimitSendPersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .limit(3)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(sender.getId(), search);
        Assertions.assertEquals(8, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(3, personalMessageInfoList.getLimit());
        Assertions.assertEquals(3, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("발신 목록 조회 - skip, limit")
    public void getSkipLimitSendPersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(1)
                .limit(2)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(sender.getId(), search);
        Assertions.assertEquals(8, personalMessageInfoList.getTotal());
        Assertions.assertEquals(1, personalMessageInfoList.getSkip());
        Assertions.assertEquals(2, personalMessageInfoList.getLimit());
        Assertions.assertEquals(2, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("발신 목록 조회 - 삭제 개인 쪽지 포함")
    public void getSendPersonalMessageListSendIncludeDelete() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);
        this.setRandomPersonalMessage(sender, receiver, true, false, 3);
        this.setRandomPersonalMessage(sender, receiver, false, true, 6);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(sender.getId(), search);
        Assertions.assertEquals(14, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(14, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회")
    public void getReceivePersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(8, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(8, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - skip")
    public void getSkipReceivePersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(1)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(8, personalMessageInfoList.getTotal());
        Assertions.assertEquals(1, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(7, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - limit")
    public void getLimitReceivePersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .limit(1)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(8, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(1, personalMessageInfoList.getLimit());
        Assertions.assertEquals(1, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - skip, limit")
    public void getSkipLimitReceivePersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(1).limit(2).build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(8, personalMessageInfoList.getTotal());
        Assertions.assertEquals(1, personalMessageInfoList.getSkip());
        Assertions.assertEquals(2, personalMessageInfoList.getLimit());
        Assertions.assertEquals(2, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - 삭제 개인 쪽지 포함")
    public void getReceivePersonalMessageListReceiveIncludeDelete() throws Exception {
        // 계정 생성
        TestAccountInfo sender = this.setRandomAccount();
        TestAccountInfo receiver = this.setRandomAccount();
        // 개인 쪽지 생성
        this.setRandomPersonalMessage(sender, receiver, 8);
        this.setRandomPersonalMessage(sender, receiver, true, false, 3);
        this.setRandomPersonalMessage(sender, receiver, false, true, 6);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(11, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(11, personalMessageInfoList.getPersonalMessageInfoList().size());
    }
}
