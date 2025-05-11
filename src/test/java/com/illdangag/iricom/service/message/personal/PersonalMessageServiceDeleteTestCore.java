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

@DisplayName("Service: 개인 쪽지 - 삭제")
public class PersonalMessageServiceDeleteTestCore extends IricomTestServiceSuite {
    @Autowired
    private PersonalMessageService personalMessageService;

    public PersonalMessageServiceDeleteTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("수신 삭제")
    public void deleteSendPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.deletePersonalMessageInfo(receiver.getId(), personalMessage.getId());

        Assertions.assertNotNull(personalMessageInfo);
        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(receiver.getId(), personalMessage.getId());
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("송신 삭제")
    public void deleteReceivePersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);;

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.deletePersonalMessageInfo(sender.getId(), personalMessage.getId());

        Assertions.assertNotNull(personalMessageInfo);
        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(sender.getId(), personalMessage.getId());
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 개인 쪽지 삭제")
    public void deleteNotExistPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        // 개인 쪽지 생성
        setRandomPersonalMessage(sender, receiver);;

        String personalMessageId = "NOT_EXIST";

        IricomException senderException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(sender.getId(), personalMessageId);
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), senderException.getErrorCode());

        IricomException receiverException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(receiver.getId(), personalMessageId);
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), receiverException.getErrorCode());
    }

    @Test
    @DisplayName("다른 사람의 개인 쪽지 삭제")
    public void deleteNotPermissionPersonalMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();
        TestAccountInfo other = setRandomAccount();
        // 개인 쪽지 생성
        TestPersonalMessageInfo personalMessage = setRandomPersonalMessage(sender, receiver);;

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(other.getId(), personalMessage.getId());
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }
}
