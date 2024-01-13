package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PersonalMessage;
import com.illdangag.iricom.server.data.request.PersonalMessageInfoCreate;
import com.illdangag.iricom.server.data.request.PersonalMessageInfoSearch;
import com.illdangag.iricom.server.data.response.PersonalMessageInfo;
import com.illdangag.iricom.server.data.response.PersonalMessageInfoList;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.PersonalMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;

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
    public PersonalMessageInfo createPersonalMessageInfo(Account account, PersonalMessageInfoCreate personalMessageInfoCreate) {
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
        // TODO
        return null;
    }

    @Override
    public PersonalMessageInfo getPersonalMessageInfo(Account account, String personalMessageId) {
        // TODO
        return null;
    }

    @Override
    public PersonalMessageInfoList getPersonalMessageInfoList(String accountId, PersonalMessageInfoSearch personalMessageInfoSearch) {
        // TODO
        return null;
    }

    @Override
    public PersonalMessageInfoList getPersonalMessageInfoList(Account account, PersonalMessageInfoSearch personalMessageInfoSearch) {
        // TODO
        return null;
    }
}
