package com.illdangag.iricom.core.repository.implement;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.PersonalMessage;
import com.illdangag.iricom.core.repository.PersonalMessageRepository;
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
                " WHERE pm.id = :id";

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
    public List<PersonalMessage> getSendPersonalMessageList(Account account, Integer offset, Integer limit) {
        final String jpql = "SELECT pm FROM PersonalMessage pm" +
                " WHERE pm.sendAccount = :account" +
                " AND pm.sendDeleted = false" +
                " ORDER BY pm.createDate DESC";

        TypedQuery<PersonalMessage> query = this.entityManager.createQuery(jpql, PersonalMessage.class)
                .setParameter("account", account);

        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public long getSendPersonalMessageCount(Account account) {
        final String jpql = "SELECT COUNT(1) FROM PersonalMessage pm" +
                " WHERE pm.sendAccount = :account" +
                " AND pm.sendDeleted = false";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getSingleResult();
    }

    @Override
    public List<PersonalMessage> getReceivePersonalMessageList(Account account, Integer offset, Integer limit) {
        final String jpql = "SELECT pm FROM PersonalMessage pm" +
                " WHERE pm.receiveAccount = :account" +
                " AND pm.receiveDeleted = false" +
                " ORDER BY pm.createDate DESC";

        TypedQuery<PersonalMessage> query = this.entityManager.createQuery(jpql, PersonalMessage.class)
                .setParameter("account", account);

        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public long getReceivePersonalMessageCount(Account account) {
        final String jpql = "SELECT COUNT(1) FROM PersonalMessage pm" +
                " WHERE pm.receiveAccount = :account" +
                " AND pm.receiveDeleted = false";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getSingleResult();
    }

    @Override
    public List<PersonalMessage> getUnreadReceivePersonalMessageList(Account account, Integer offset, Integer limit) {
        final String jpql = "SELECT pm FROM PersonalMessage pm" +
                " WHERE pm.receiveAccount = :account" +
                " AND pm.receiveDeleted = false" +
                " AND pm.receivedConfirm = false" +
                " ORDER BY pm.createDate DESC";

        TypedQuery<PersonalMessage> query = this.entityManager.createQuery(jpql, PersonalMessage.class)
                .setParameter("account", account);

        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public long getUnreadReceivePersonalMessageCount(Account account) {
        final String jpql = "SELECT COUNT(1) FROM PersonalMessage pm" +
                " WHERE pm.receiveAccount = :account" +
                " AND pm.receiveDeleted = false" +
                " AND pm.receivedConfirm = false";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getSingleResult();
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
