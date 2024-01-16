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

@DisplayName("Service: 개인 쪽지 - 삭제")
public class PersonalMessageServiceDeleteTest extends IricomTestSuite {
    @Autowired
    private PersonalMessageService personalMessageService;

    public PersonalMessageServiceDeleteTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("수신 삭제")
    public void deleteSendPersonalMessage() throws Exception {
        TestPersonalMessageInfo testPersonalMessageInfo = TestPersonalMessageInfo.builder()
                .sender(common00).receiver(common01)
                .title("TITLE").message("MESSAGE")
                .build();

        addTestPersonalMessageInfo(testPersonalMessageInfo);
        init();

        String accountId = getAccountId(common00);
        String personalMessageId = getPersonalMessageId(testPersonalMessageInfo);
        PersonalMessageInfo personalMessageInfo = this.personalMessageService.deletePersonalMessageInfo(accountId, personalMessageId);

        Assertions.assertNotNull(personalMessageInfo);
        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.personalMessageService.getPersonalMessageInfo(accountId, personalMessageId);
        });
        Assertions.assertEquals(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE.getCode(), iricomException.getErrorCode());
    }

    @Test
    @DisplayName("송신 삭제")
    public void deleteReceivePersonalMessage() throws Exception {
        // TODO
    }
}
