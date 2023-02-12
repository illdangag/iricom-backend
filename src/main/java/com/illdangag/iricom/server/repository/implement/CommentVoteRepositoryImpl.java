package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.CommentVote;
import com.illdangag.iricom.server.data.entity.VoteType;
import com.illdangag.iricom.server.repository.CommentVoteRepository;
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
public class CommentVoteRepositoryImpl implements CommentVoteRepository {
    private final EntityManager entityManager;

    @Autowired
    public CommentVoteRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Optional<CommentVote> getCommentVote(Account account, Comment comment, VoteType type) {
        String jpql = "SELECT cv FROM CommentVote cv " +
                "WHERE cv.account = :account " +
                "AND cv.comment = :comment " +
                "AND cv.type = :type";

        TypedQuery<CommentVote> query = this.entityManager.createQuery(jpql, CommentVote.class)
                .setParameter("account", account)
                .setParameter("comment", comment)
                .setParameter("type", type);
        List<CommentVote> resultList = query.getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public long getCommentVoteCount(Comment comment, VoteType type) {
        String jpql = "SELECT COUNT(*) FROM CommentVote cv " +
                "WHERE cv.comment = :comment " +
                "AND cv.type = :type";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("comment", comment)
                .setParameter("type", type);

        return query.getSingleResult();
    }

    @Override
    public void save(CommentVote commentVote) {
        EntityTransaction transaction = this.entityManager.getTransaction();
        transaction.begin();
        if (commentVote.getId() == null) {
            this.entityManager.persist(commentVote);
        } else {
            this.entityManager.merge(commentVote);
        }
        transaction.commit();
    }
}
