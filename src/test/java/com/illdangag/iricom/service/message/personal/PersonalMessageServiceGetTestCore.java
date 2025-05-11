package com.illdangag.iricom.service.message.personal;

import com.illdangag.iricom.core.data.response.PersonalMessageInfo;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
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

@DisplayName("Service: 개인 쪽지 - 조회")
@Transactional
public class PersonalMessageServiceGetTestCore extends IricomTestServiceSuite {
    @Autowired
    private PersonalMessageService personalMessageService;

    public PersonalMessageServiceGetTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("조회")
    public void getPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.getPersonalMessageInfo(sender.getId(), personalMessage.getId());

        Assertions.assertEquals(personalMessage.getId(), personalMessageInfo.getId());
        Assertions.assertEquals(personalMessage.getTitle(), personalMessageInfo.getTitle());
        Assertions.assertEquals(personalMessage.getMessage(), personalMessageInfo.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 개인 쪽지 조회")
    public void getNotExistPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(sender.getId(), "NOT_EXIST_ID");
        });

        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("다른 계정이 보내거나 받은 개인 쪽지 조회")
    public void getOtherPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        TestAccountInfo other = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(other.getId(), personalMessage.getId());
        });

        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("수신자가 내용 확인 시 수신 확인")
    public void getPersonalMessageReceivedConfirm() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        // 수신자가 확인 하기전 발신자가 조회
        PersonalMessageInfo senderPersonalMessageInfo = this.personalMessageService.getPersonalMessageInfo(sender.getId(), personalMessage.getId());
        Assertions.assertFalse(senderPersonalMessageInfo.getReceivedConfirm()); // 수신자 확인전

        // 수신자가 해당 내용 조회
        PersonalMessageInfo receiverPersonalMessageInfo = this.personalMessageService.getPersonalMessageInfo(receiver.getId(), personalMessage.getId());
        Assertions.assertTrue(receiverPersonalMessageInfo.getReceivedConfirm()); // 수신자 확인

        // 수신자가 확인 후 발신자가 조회
        PersonalMessageInfo confirmPersonalMessageInfo = this.personalMessageService.getPersonalMessageInfo(sender.getId(), personalMessage.getId());
        Assertions.assertTrue(confirmPersonalMessageInfo.getReceivedConfirm()); // 수신자 확인후
    }

    @Test
    @DisplayName("발신 메시지 조회")
    public void getReceivePersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.getSendPersonalMessageInfo(sender.getId(), personalMessage.getId());
        Assertions.assertNotNull(personalMessageInfo);
        Assertions.assertEquals(personalMessage.getId(), personalMessageInfo.getId());
    }

    @Test
    @DisplayName("발신한 메시지를 수신 메시지 형식으로 조회")
    public void getReceivePersonalMessageBySendPersonalMessageType() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getSendPersonalMessageInfo(receiver.getId(), personalMessage.getId());
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 발신 메시지 조회")
    public void getNotExistReceivePersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getSendPersonalMessageInfo(sender.getId(), "NOT_EXIST_PERSONAL_MESSAGE_ID");
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("발신하지 않은 발신 메시지 조회")
    public void getNotReceivePersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        TestAccountInfo other = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getReceivePersonalMessageInfo(other.getId(), personalMessage.getId());
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("수신 메시지 조회")
    public void getSendPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.getReceivePersonalMessageInfo(receiver.getId(), personalMessage.getId());
        Assertions.assertNotNull(personalMessageInfo);
        Assertions.assertEquals(personalMessage.getId(), personalMessageInfo.getId());
    }

    @Test
    @DisplayName("존재하지 않는 수신 메시지 조회")
    public void getNotExistSendPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getSendPersonalMessageInfo(receiver.getId(), "NOT_EXIST_PERSONAL_MESSAGE_ID");
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("수신 하지 않은 수신 메시지 조회")
    public void getNotSendPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        TestAccountInfo other = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getSendPersonalMessageInfo(other.getId(), personalMessage.getId());
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }
}
