package com.illdangag.iricom.core.repository.implement;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Post;
import com.illdangag.iricom.core.data.entity.PostVote;
import com.illdangag.iricom.core.data.entity.type.VoteType;
import com.illdangag.iricom.core.repository.PostVoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class PostVoteRepositoryImpl implements PostVoteRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<PostVote> getPostVote(Account account, Post post, VoteType voteType) {
        final String jpql = "SELECT pv from PostVote pv" +
                " WHERE pv.account = :account" +
                " AND pv.post = :post" +
                " AND pv.type = :type";

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
        final String jpql = "SELECT COUNT(*) FROM PostVote pv" +
                " WHERE pv.post = :post" +
                " AND pv.type = :type";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("type", voteType);
        return query.getSingleResult();
    }

    @Override
    public void save(PostVote postVote) {
        if (postVote.getId() == null) {
            this.entityManager.persist(postVote);
        } else {
            this.entityManager.merge(postVote);
        }
        this.entityManager.flush();
    }
}
