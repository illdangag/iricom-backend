package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostVote;
import com.illdangag.iricom.server.data.entity.VoteType;
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
    private final EntityManager entityManager;

    @Autowired
    public PostVoteRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Optional<PostVote> getPostVote(Account account, Post post, VoteType voteType) {
        this.entityManager.clear();
        final String jpql = "SELECT pv from PostVote pv " +
                "WHERE pv.account = :account " +
                "AND pv.post = :post " +
                "AND pv.type = :type";

        TypedQuery<PostVote> query = this.entityManager.createQuery(jpql, PostVote.class)
                .setParameter("account", account)
                .setParameter("post", post)
                .setParameter("type", voteType);

        List<PostVote> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public long getPostVoteCount(Post post, VoteType voteType) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM PostVote pv " +
                "WHERE pv.post = :post " +
                "AND pv.type = :type";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("type", voteType);

        return query.getSingleResult();
    }

    @Override
    public void save(PostVote postVote) {
        EntityTransaction transaction = this.entityManager.getTransaction();
        transaction.begin();
        if (postVote.getId() == null) {
            this.entityManager.persist(postVote);
        } else {
            this.entityManager.merge(postVote);
        }
        transaction.commit();
    }
}
