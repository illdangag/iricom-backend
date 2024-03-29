package com.illdangag.iricom.server.service.message.personal;

import com.illdangag.iricom.server.data.response.PersonalMessageInfo;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PersonalMessageService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestPersonalMessageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;

@DisplayName("Service: 개인 쪽지 - 조회")
@Transactional
public class PersonalMessageServiceGetTest extends IricomTestSuite {
    @Autowired
    private PersonalMessageService personalMessageService;

    public PersonalMessageServiceGetTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("조회")
    public void getPersonalMessage() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String sendAccountId = getAccountId(common00);

        PersonalMessageInfo personalMessageInfo = getPersonalMessage(testPersonalMessageInfo);
        this.personalMessageService.getPersonalMessageInfo(sendAccountId, personalMessageInfo.getId());

        Assertions.assertEquals(testPersonalMessageInfo.getTitle(), personalMessageInfo.getTitle());
        Assertions.assertEquals(testPersonalMessageInfo.getMessage(), personalMessageInfo.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 개인 쪽지 조회")
    public void getNotExistPersonalMessage() throws Exception {
        String accountId = getAccountId(common00);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(accountId, "NOT_EXIST_ID");
        });

        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("다른 계정이 보내거나 받은 개인 쪽지 조회")
    public void getOtherPersonalMessage() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common02);

        PersonalMessageInfo personalMessageInfo = getPersonalMessage(testPersonalMessageInfo);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(accountId, personalMessageInfo.getId());
        });

        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("수신자가 내용 확인 시 수신 확인")
    public void getPersonalMessageReceivedConfirm() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common01);
        PersonalMessageInfo personalMessageInfo = getPersonalMessage(testPersonalMessageInfo);

        PersonalMessageInfo resultPersonalMessageInfo = this.personalMessageService.getPersonalMessageInfo(accountId, personalMessageInfo.getId());
        Assertions.assertTrue(resultPersonalMessageInfo.getReceivedConfirm());
    }

    @Test
    @DisplayName("발신자가 내용 확인시 수식 확인 하지 않음")
    public void getPersonalMessageSendConfirm() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common00);
        String personalMessageInfoId = getPersonalMessageId(testPersonalMessageInfo);

        PersonalMessageInfo resultPersonalMessageInfo = this.personalMessageService.getPersonalMessageInfo(accountId, personalMessageInfoId);
        Assertions.assertFalse(resultPersonalMessageInfo.getReceivedConfirm());
    }

    @Test
    @DisplayName("송신 메시지 조회")
    public void getReceivePersonalMessage() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common01);
        String personalMessageInfoId = getPersonalMessageId(testPersonalMessageInfo);

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.getReceivePersonalMessageInfo(accountId, personalMessageInfoId);
        Assertions.assertNotNull(personalMessageInfo);
        Assertions.assertEquals(personalMessageInfoId, personalMessageInfo.getId());
    }

    @Test
    @DisplayName("존재하지 않는 송신 메시지 조회")
    public void getNotExistReceivePersonalMessage() throws Exception {
        String accountId = getAccountId(common01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getReceivePersonalMessageInfo(accountId, "NOT_EXIST");
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("송신하지 않은 송신 메시지 조회")
    public void getNotReceivePersonalMessage() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common00);
        String personalMessageInfoId = getPersonalMessageId(testPersonalMessageInfo);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getReceivePersonalMessageInfo(accountId, personalMessageInfoId);
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("수신 메시지 조회")
    public void getSendPersonalMessage() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common00);
        String personalMessageInfoId = getPersonalMessageId(testPersonalMessageInfo);

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.getSendPersonalMessageInfo(accountId, personalMessageInfoId);
        Assertions.assertNotNull(personalMessageInfo);
        Assertions.assertEquals(personalMessageInfoId, personalMessageInfo.getId());
    }

    @Test
    @DisplayName("존재하지 않는 수신 메시지 조회")
    public void getNotExistSendPersonalMessage() throws Exception {
        String accountId = getAccountId(common01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getSendPersonalMessageInfo(accountId, "NOT_EXIST");
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("송신하지 않은 송신 메시지 조회")
    public void getNotSendPersonalMessage() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common01);
        String personalMessageInfoId = getPersonalMessageId(testPersonalMessageInfo);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getSendPersonalMessageInfo(accountId, personalMessageInfoId);
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }
}
