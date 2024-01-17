package com.illdangag.iricom.server.service.message.personal;

import com.illdangag.iricom.server.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.server.data.response.PersonalMessageInfoList;
import com.illdangag.iricom.server.service.PersonalMessageService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestPersonalMessageInfo;
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
    @DisplayName("송신 목록 조회")
    public void getSendPersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common03)
                .title("TITLE").message("MESSAGE")
                .build();


        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(4, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(4, personalMessageInfoList.getTotal());
    }

    @Test
    @DisplayName("송신 목록 조회 - skip")
    public void getSkipSendPersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common03)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(1)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(4, personalMessageInfoList.getTotal());
        Assertions.assertEquals(1, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(3, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("송신 목록 조회 - limit")
    public void getLimitSendPersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common03)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .limit(3)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(4, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(3, personalMessageInfoList.getLimit());
        Assertions.assertEquals(3, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("송신 목록 조회 - skip, limit")
    public void getSkipLimitSendPersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(systemAdmin).receiver(common03)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(1)
                .limit(2)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(4, personalMessageInfoList.getTotal());
        Assertions.assertEquals(1, personalMessageInfoList.getSkip());
        Assertions.assertEquals(2, personalMessageInfoList.getLimit());
        Assertions.assertEquals(2, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("송신 목록 조회 - 송신자 삭제 개인 쪽지 포함")
    public void getSendPersonalMessageListSendIncludeDelete() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common04).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common04).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common04).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE").sendDeleted(true)
                .build();
        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02);
        init();

        String accountId = getAccountId(common04);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(2, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(2, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("송신 목록 조회 - 수신자 삭제 개인 쪽지 포함")
    public void getSendPersonalMessageListReceiveIncludeDelete() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common05).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common05).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common05).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE").receiveDeleted(true)
                .build();
        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02);
        init();

        String accountId = getAccountId(common05);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getSendPersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(3, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(3, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회")
    public void getReceivePersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common01).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(3, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(3, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - skip")
    public void getSkipReceivePersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common01).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(1)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(3, personalMessageInfoList.getTotal());
        Assertions.assertEquals(1, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(2, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - limit")
    public void getLimitReceivePersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common01).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .limit(1)
                .build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(3, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(1, personalMessageInfoList.getLimit());
        Assertions.assertEquals(1, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - skip, limit")
    public void getSkipLimitReceivePersonalMessageList() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common01).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo03 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common02)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02,
                testPersonalMessageInfo03);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder()
                .skip(1).limit(2).build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(3, personalMessageInfoList.getTotal());
        Assertions.assertEquals(1, personalMessageInfoList.getSkip());
        Assertions.assertEquals(2, personalMessageInfoList.getLimit());
        Assertions.assertEquals(2, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - 수신자 삭제 개인 쪽지 포함")
    public void getReceivePersonalMessageListReceiveIncludeDelete() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE").receiveDeleted(true)
                .build();
        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(2, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(2, personalMessageInfoList.getPersonalMessageInfoList().size());
    }

    @Test
    @DisplayName("수신 목록 조회 - 발신자 삭제 개인 쪽지 포함")
    public void getReceivePersonalMessageListSendIncludeDelete() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo00 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo01 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE")
                .build();
        TestPersonalMessageInfo testPersonalMessageInfo02 = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(systemAdmin)
                .title("TITLE").message("MESSAGE").sendDeleted(true)
                .build();
        addTestPersonalMessageInfo(testPersonalMessageInfo00, testPersonalMessageInfo01, testPersonalMessageInfo02);
        init();

        String accountId = getAccountId(systemAdmin);

        PersonalMessageInfoSearch search = PersonalMessageInfoSearch.builder().build();
        PersonalMessageInfoList personalMessageInfoList = this.personalMessageService.getReceivePersonalMessageInfoList(accountId, search);
        Assertions.assertEquals(3, personalMessageInfoList.getTotal());
        Assertions.assertEquals(0, personalMessageInfoList.getSkip());
        Assertions.assertEquals(20, personalMessageInfoList.getLimit());
        Assertions.assertEquals(3, personalMessageInfoList.getPersonalMessageInfoList().size());
    }
}
