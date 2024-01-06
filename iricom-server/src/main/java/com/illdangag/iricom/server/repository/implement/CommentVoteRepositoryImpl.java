package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.CommentVote;
import com.illdangag.iricom.server.data.entity.type.VoteType;
import com.illdangag.iricom.server.repository.CommentVoteRepository;
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
public class CommentVoteRepositoryImpl implements CommentVoteRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CommentVote> getCommentVote(Account account, Comment comment, VoteType type) {
        final String jpql = "SELECT cv FROM CommentVote cv" +
                " WHERE cv.account = :account" +
                " AND cv.comment = :comment" +
                " AND cv.type = :type";

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
        final String jpql = "SELECT COUNT(*) FROM CommentVote cv" +
                " WHERE cv.comment = :comment" +
                " AND cv.type = :type";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("comment", comment)
                .setParameter("type", type);
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public void save(CommentVote commentVote) {
        if (commentVote.getId() == null) {
            this.entityManager.persist(commentVote);
        } else {
            this.entityManager.merge(commentVote);
        }
    }
}
