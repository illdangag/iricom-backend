package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PersonalMessage;
import com.illdangag.iricom.server.repository.PersonalMessageRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class PersonalMessageRepositoryImpl implements PersonalMessageRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<PersonalMessage> getPersonalMessage(Long id) {
        final String jpql = "SELECT pm FROM PersonalMessage pm" +
                " WHERE pm.id = :id" +
                " AND pm.deleted = false";

        TypedQuery<PersonalMessage> query = this.entityManager.createQuery(jpql, PersonalMessage.class)
                .setParameter("id", id);
        PersonalMessage result = query.getSingleResult();

        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    @Override
    public List<PersonalMessage> getSendPersonalMessageList(Account account) {
        final String jpql = "SELECT pm FROM PersonalMessage pm" +
                " WHERE pm.sendAccount = :account" +
                " AND pm.deleted = false";

        TypedQuery<PersonalMessage> query = this.entityManager.createQuery(jpql, PersonalMessage.class)
                .setParameter("account", account);

        return query.getResultList();
    }

    @Override
    public List<PersonalMessage> getReceivedPersonalMessageList(Account account) {
        final String jpql = "SELECT pm FROM PersonalMessage pm" +
                " WHERE pm.receiveAccount = :account" +
                " AND pm.deleted = false";

        TypedQuery<PersonalMessage> query = this.entityManager.createQuery(jpql, PersonalMessage.class)
                .setParameter("account", account);

        return query.getResultList();
    }

    @Override
    public void save(PersonalMessage personalMessage) {
        if (personalMessage.getId() == null) {
            this.entityManager.persist(personalMessage);
        } else {
            this.entityManager.merge(personalMessage);
        }
        this.entityManager.flush();
    }
}
