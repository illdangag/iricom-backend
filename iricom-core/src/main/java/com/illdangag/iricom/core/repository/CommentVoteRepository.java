package com.illdangag.iricom.core.repository;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Comment;
import com.illdangag.iricom.core.data.entity.CommentVote;
import com.illdangag.iricom.core.data.entity.type.VoteType;

import java.util.Optional;

public interface CommentVoteRepository {
    Optional<CommentVote> getCommentVote(Account account, Comment comment, VoteType type);

    long getCommentVoteCount(Comment comment, VoteType type);

    void save(CommentVote commentVote);
}
