package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostVote;
import com.illdangag.iricom.server.data.entity.type.VoteType;
import com.illdangag.iricom.server.repository.PostVoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class PostVoteRepositoryImpl implements PostVoteRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public PostVoteRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<PostVote> getPostVote(Account account, Post post, VoteType voteType) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pv from PostVote pv" +
                " WHERE pv.account = :account" +
                " AND pv.post = :post" +
                " AND pv.type = :type";

        TypedQuery<PostVote> query = entityManager.createQuery(jpql, PostVote.class)
                .setParameter("account", account)
                .setParameter("post", post)
                .setParameter("type", voteType);

        List<PostVote> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public long getPostVoteCount(Post post, VoteType voteType) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostVote pv" +
                " WHERE pv.post = :post" +
                " AND pv.type = :type";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("type", voteType);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public void save(PostVote postVote) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (postVote.getId() == null) {
            entityManager.persist(postVote);
        } else {
            entityManager.merge(postVote);
        }
        transaction.commit();
        entityManager.close();
    }
}
