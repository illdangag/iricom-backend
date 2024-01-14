package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PersonalMessage;
import com.illdangag.iricom.server.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.server.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.server.data.response.PersonalMessageInfo;
import com.illdangag.iricom.server.data.response.PersonalMessageInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.PersonalMessageService;
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
        String receiverAccountId = personalMessageInfoCreate.getReceiverAccountId();

        Account receiverAccount = this.getAccount(receiverAccountId);

        PersonalMessage personalMessage = PersonalMessage.builder()
                .sendAccount(account).receiveAccount(receiverAccount)
                .title(personalMessageInfoCreate.getTitle())
                .message(personalMessageInfoCreate.getMessage())
                .build();

        this.personalMessageRepository.save(personalMessage);
        return new PersonalMessageInfo(personalMessage);
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

        return new PersonalMessageInfo(personalMessage);
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

        List<PersonalMessage> personalMessageList = this.personalMessageRepository.getReceivePersonalMessageList(account, skip, limit);
        long total = this.personalMessageRepository.getReceivePersonalMessageCount(account);

        List<PersonalMessageInfo> personalMessageInfoList = personalMessageList.stream()
                .map(PersonalMessageInfo::new)
                .collect(Collectors.toList());

        return PersonalMessageInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .personalMessageInfoList(personalMessageInfoList)
                .build();
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
                .map(PersonalMessageInfo::new)
                .collect(Collectors.toList());

        return PersonalMessageInfoList.builder()
                .total(total)
                .skip(skip)
                .limit(limit)
                .personalMessageInfoList(personalMessageInfoList)
                .build();
    }

    private PersonalMessage getPersonalMessage(String id) {
        long personalMessageId = -1;
        try {
            personalMessageId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE);
        }

        Optional<PersonalMessage> personalMessageOptional = this.personalMessageRepository.getPersonalMessage(personalMessageId);
        return personalMessageOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_PERSONAL_MESSAGE));
    }
}
