package com.illdangag.iricom.server.service.message.personal;

import com.illdangag.iricom.server.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.server.data.response.PersonalMessageInfo;
import com.illdangag.iricom.server.service.PersonalMessageService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
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
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiveAccountId(receiver.getId())
                .title("TITLE").message("MESSAGE")
                .build();

        PersonalMessageInfo personalMessageInfo = this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        Assertions.assertNotNull(personalMessageInfo);
        Assertions.assertNotNull(personalMessageInfo.getId());
        Assertions.assertEquals("TITLE", personalMessageInfo.getTitle());
        Assertions.assertEquals("MESSAGE", personalMessageInfo.getMessage());
    }

    @Test
    @DisplayName("수신자 미설정")
    public void notExistReceiver() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .title("TITLE").message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("제목 미설정")
    public void notExistTitle() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiveAccountId(receiver.getId())
                .message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("제목 빈문자열")
    public void emptyTitle() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiveAccountId(receiver.getId())
                .title("").message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("제목 공백 문자열")
    public void whiteSpaceTitle() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiveAccountId(receiver.getId())
                .title("        ").message("MESSAGE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("내용 미설정")
    public void notExistMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiveAccountId(receiver.getId())
                .title("TITLE")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("내용 빈문자열")
    public void emptyMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiveAccountId(receiver.getId())
                .title("TITLE").message("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        });
    }

    @Test
    @DisplayName("내용 빈문자열")
    public void whiteSpaceMessage() throws Exception {
        // 계정 생성
        TestAccountInfo sender = setRandomAccount();
        TestAccountInfo receiver = setRandomAccount();

        PersonalMessageInfoCreate personalMessageInfoCreate = PersonalMessageInfoCreate.builder()
                .receiveAccountId(receiver.getId())
                .title("TITLE").message("     ")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.personalMessageService.createPersonalMessageInfo(sender.getId(), personalMessageInfoCreate);
        });
    }
}
