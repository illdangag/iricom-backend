package com.illdangag.iricom.service.message.personal;

import com.illdangag.iricom.core.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.core.data.request.PersonalMessageStatus;
import com.illdangag.iricom.core.data.response.PersonalMessageInfoList;
import com.illdangag.iricom.core.service.PersonalMessageService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPersonalMessageInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.List;

@DisplayName("Service: 개인 쪽지 - 목록 조회")
@Transactional
public class PersonalMessageServiceSearchTestCore extends IricomTestServiceSuite {
    @Autowired
    private PersonalMessageService personalMessageService;

    public PersonalMessageServiceSearchTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("발신 목록 조회")
    public void getSendPersonalMessageList() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 20);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);
        setRandomPersonalMessage(sender, receiver, true, false, 3);
        setRandomPersonalMessage(sender, receiver, false, true, 6);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);

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
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver, 8);
        setRandomPersonalMessage(sender, receiver, true, false, 3);
        setRandomPersonalMessage(sender, receiver, false, true, 6);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(11, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(11, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - 읽지 않은 쪽지")
    public void getUnreadReceivePersonalMessageList() {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        List<TestPersonalMessageInfo> personalMessageList = setRandomPersonalMessage(sender, receiver, 5);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .status(PersonalMessageStatus.UNREAD)
                .build();
        PersonalMessageInfoList beforePersonalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(5, beforePersonalMessageInfoList.getTotal());

        // 2개의 개인 쪽지 조회
        this.personalMessageService.getPersonalMessageInfo(receiver.getId(), personalMessageList.get(0).getId());
        this.personalMessageService.getPersonalMessageInfo(receiver.getId(), personalMessageList.get(1).getId());

        PersonalMessageInfoList afterPersonalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(receiver.getId(), search);
        Assertions.assertEquals(3, afterPersonalMessageInfoList.getTotal());
    }
}
