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
}
