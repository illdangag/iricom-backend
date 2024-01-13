package com.illdangag.iricom.server.service.message.personal;

import com.illdangag.iricom.server.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.server.data.response.PersonalMessageInfo;
import com.illdangag.iricom.server.service.PersonalMessageService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

@DisplayName("service: 개인 쪽지 - 생성")
@Transactional
public class PersonalMessageServiceCreateTest extends IricomTestSuite {
    @Autowired
    private PersonalMessageService personalMessageService;

    public PersonalMessageServiceCreateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("쪽지 생성")
    public void createPersonalMessage() throws Exception {
        String sendAccountId = getAccountId(common00);
        String receiveAccountId = getAccountId(common01);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiverAccountId(receiveAccountId)
                .title("TITLE").message("MESSAGE")
                .build();

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        Assertions.assertNotNull(personalMessageInfo);
        Assertions.assertNotNull(personalMessageInfo.getId());
        Assertions.assertEquals("TITLE", personalMessageInfo.getTitle());
        Assertions.assertEquals("MESSAGE", personalMessageInfo.getMessage());
    }

    @Test
    @DisplayName("수신자 미설정")
    public void notExistReceiver() throws Exception {
        String sendAccountId = getAccountId(common00);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .title("TITLE").message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("제목 미설정")
    public void notExistTitle() throws Exception {
        String sendAccountId = getAccountId(common00);
        String receiveAccountId = getAccountId(common01);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiverAccountId(receiveAccountId)
                .message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("제목 빈문자열")
    public void emptyTitle() throws Exception {
        String sendAccountId = getAccountId(common00);
        String receiveAccountId = getAccountId(common01);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiverAccountId(receiveAccountId)
                .title("").message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("제목 공백 문자열")
    public void whiteSpaceTitle() throws Exception {
        String sendAccountId = getAccountId(common00);
        String receiveAccountId = getAccountId(common01);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiverAccountId(receiveAccountId)
                .title("        ").message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("내용 미설정")
    public void notExistMessage() throws Exception {
        String sendAccountId = getAccountId(common00);
        String receiveAccountId = getAccountId(common01);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiverAccountId(receiveAccountId)
                .title("TITLE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("내용 빈문자열")
    public void emptyMessage() throws Exception {
        String sendAccountId = getAccountId(common00);
        String receiveAccountId = getAccountId(common01);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiverAccountId(receiveAccountId)
                .title("TITLE").message("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("내용 빈문자열")
    public void whiteSpaceMessage() throws Exception {
        String sendAccountId = getAccountId(common00);
        String receiveAccountId = getAccountId(common01);

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiverAccountId(receiveAccountId)
                .title("TITLE").message("     ")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sendAccountId, personalMessageInfoCreate);
        });
    }
}
