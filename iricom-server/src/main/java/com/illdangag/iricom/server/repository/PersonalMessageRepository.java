package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PersonalMessage;

import java.util.List;
import java.util.Optional;

public interface PersonalMessageRepository {
    Optional<PersonalMessage> getPersonalMessage(Long id);

    List<PersonalMessage> getSendPersonalMessageList(Account account);

    List<PersonalMessage> getReceivedPersonalMessageList(Account account);

    void save(PersonalMessage personalMessage);
}
