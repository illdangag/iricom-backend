package com.illdangag.iricom.core.service.implement;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.PersonalMessage;
import com.illdangag.iricom.core.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.core.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.core.data.request.PersonalMessageStatus;
import com.illdangag.iricom.core.data.response.PersonalMessageInfo;
import com.illdangag.iricom.core.data.response.PersonalMessageInfoList;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.repository.*;
import com.illdangag.iricom.core.service.PersonalMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Transactional
@Service
public class PersonalMessageServiceImpl extends IricomService implements PersonalMessageService {

    private final PersonalMessageRepository personalMessageRepository;

    @Autowired
    public PersonalMessageServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                                      PostRepository postRepository, CommentRepository commentRepository,
                                      PersonalMessageRepository personalMessageRepository) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
        this.personalMessageRepository = personalMessageRepository;
    }

    @Override
    public PersonalMessageInfo createPersonalMessageInfo(String accountId, @Valid PersonalMessageInfoCreate personalMessageInfoCreate) {
        Account account = this.getAccount(accountId);
        return this.createPersonalMessageInfo(account, personalMessageInfoCreate);
    }

    @Override
    public PersonalMessageInfo createPersonalMessageInfo(Account account, @Valid PersonalMessageInfoCreate personalMessageInfoCreate) {
        String receiverAccountId = personalMessageInfoCreate.getReceiveAccountId();

        Account receiverAccount = this.getAccount(receiverAccountId);

        PersonalMessage personalMessage = PersonalMessage.builder()
                .sendAccount(account).receiveAccount(receiverAccount)
                .title(personalMessageInfoCreate.getTitle())
                .message(personalMessageInfoCreate.getMessage())
                .build();

        this.personalMessageRepository.save(personalMessage);
        return new PersonalMessageInfo(personalMessage, true);
    }

    @Override
    public PersonalMessageInfo getPersonalMessageInfo(String accountId, String personalMessageId) {
        Account account = this.getAccount(accountId);
        return this.getPersonalMessageInfo(account, personalMessageId);
    }

    @Override
    public PersonalMessageInfo getPersonalMessageInfo(Account account, String personalMessageId) {
        PersonalMessage personalMessage = this.getPersonalMessage(personalMessageId);

        if (!personalMessage.getReceiveAccount().equals(account) && !personalMessage.getSendAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE);
        }

        // 수신자 또는 송신자가 삭제한 경우
        if (personalMessage.getReceiveAccount().equals(account) && personalMessage.getReceiveDeleted()
                || personalMessage.getSendAccount().equals(account) && personalMessage.getSendDeleted()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE);
        }

        // 수신 확인 여부 저장
        boolean receivedConfirm = personalMessage.getReceivedConfirm() != null && personalMessage.getReceivedConfirm();
        if (!receivedConfirm && personalMessage.getReceiveAccount().equals(account)) {
            personalMessage.setReceivedConfirm(true);
            this.personalMessageRepository.save(personalMessage);
        }

        return new PersonalMessageInfo(personalMessage, true);
    }

    @Override
    public PersonalMessageInfo getReceivePersonalMessageInfo(String accountId, String personalMessageId) {
        Account account = this.getAccount(accountId);
        return this.getReceivePersonalMessageInfo(account, personalMessageId);
    }

    @Override
    public PersonalMessageInfo getReceivePersonalMessageInfo(Account account, String personalMessageId) {
        PersonalMessage personalMessage = this.getPersonalMessage(personalMessageId);

        // 수신한 개인 쪽지가 아니거나 수신자가 삭제한 메시지인 경우
        if (!personalMessage.getReceiveAccount().equals(account) || personalMessage.getReceiveDeleted()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE);
        }

        // 수신 확인 여부 저장
        boolean receivedConfirm = personalMessage.getReceivedConfirm();
        if (!receivedConfirm) {
            personalMessage.setReceivedConfirm(true);
            this.personalMessageRepository.save(personalMessage);
        }

        return new PersonalMessageInfo(personalMessage, true);
    }

    @Override
    public PersonalMessageInfoList getReceivePersonalMessageInfoList(String accountId, @Valid PersonalMessageInfoSearch personalMessageInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getReceivePersonalMessageInfoList(account, personalMessageInfoSearch);
    }

    @Override
    public PersonalMessageInfoList getReceivePersonalMessageInfoList(Account account, @Valid PersonalMessageInfoSearch personalMessageInfoSearch) {
        int skip = personalMessageInfoSearch.getSkip();
        int limit = personalMessageInfoSearch.getLimit();
        PersonalMessageStatus status = personalMessageInfoSearch.getStatus();

        List<PersonalMessage> personalMessageList;
        long total;

        if (status == PersonalMessageStatus.ALL) {
            personalMessageList = this.personalMessageRepository.getReceivePersonalMessageList(account, skip, limit);
            total = this.personalMessageRepository.getReceivePersonalMessageCount(account);
        } else {
            personalMessageList = this.personalMessageRepository.getUnreadReceivePersonalMessageList(account, skip, limit);
            total = this.personalMessageRepository.getUnreadReceivePersonalMessageCount(account);
        }


        List<PersonalMessageInfo> personalMessageInfoList = personalMessageList.stream()
                .map((PersonalMessage personalMessage) -> new PersonalMessageInfo(personalMessage, false))
                .collect(Collectors.toList());

        return PersonalMessageInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .personalMessageInfoList(personalMessageInfoList)
                .build();
    }

    @Override
    public PersonalMessageInfo getSendPersonalMessageInfo(String accountId, String personalMessageId) {
        Account account = this.getAccount(accountId);
        return this.getSendPersonalMessageInfo(account, personalMessageId);
    }

    @Override
    public PersonalMessageInfo getSendPersonalMessageInfo(Account account, String personalMessageId) {
        PersonalMessage personalMessage = this.getPersonalMessage(personalMessageId);

        // 송신한 개인 쪽지가 아니거나 송신자가 삭제한 메시지인 경우
        if (!personalMessage.getSendAccount().equals(account) || personalMessage.getSendDeleted()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE);
        }

        return new PersonalMessageInfo(personalMessage, true);
    }

    @Override
    public PersonalMessageInfoList getSendPersonalMessageInfoList(String accountId, @Valid PersonalMessageInfoSearch personalMessageInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getSendPersonalMessageInfoList(account, personalMessageInfoSearch);
    }

    @Override
    public PersonalMessageInfoList getSendPersonalMessageInfoList(Account account, @Valid PersonalMessageInfoSearch personalMessageInfoSearch) {
        int skip = personalMessageInfoSearch.getSkip();
        int limit = personalMessageInfoSearch.getLimit();

        List<PersonalMessage> personalMessageList = this.personalMessageRepository.getSendPersonalMessageList(account, skip, limit);
        long total = this.personalMessageRepository.getSendPersonalMessageCount(account);

        List<PersonalMessageInfo> personalMessageInfoList = personalMessageList.stream()
                .map((PersonalMessage personalMessage) -> new PersonalMessageInfo(personalMessage, false))
                .collect(Collectors.toList());

        return PersonalMessageInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .personalMessageInfoList(personalMessageInfoList)
                .build();
    }

    @Override
    public PersonalMessageInfo deletePersonalMessageInfo(String accountId, String personalMessageId) {
        Account account = this.getAccount(accountId);
        return this.deletePersonalMessageInfo(account, personalMessageId);
    }

    @Override
    public PersonalMessageInfo deletePersonalMessageInfo(Account account, String personalMessageId) {
        PersonalMessage personalMessage = this.getPersonalMessage(personalMessageId);

        if (!personalMessage.getReceiveAccount().equals(account)
                && !personalMessage.getSendAccount().equals(account)) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE);
        }

        if (personalMessage.getReceiveAccount().equals(account)) {
            personalMessage.setReceiveDeleted(true);
        } else {
            personalMessage.setSendDeleted(true);
        }

        this.personalMessageRepository.save(personalMessage);

        return new PersonalMessageInfo(personalMessage, true);
    }

    private PersonalMessage getPersonalMessage(String personalMessageId) {
        long id = -1;
        try {
            id = Long.parseLong(personalMessageId);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE);
        }

        Optional<PersonalMessage> personalMessageOptional = this.personalMessageRepository.getPersonalMessage(id);
        return personalMessageOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE));
    }
}
