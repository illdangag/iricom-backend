package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.CommentVote;
import com.illdangag.iricom.server.data.entity.type.VoteType;

import java.util.Optional;

public interface CommentVoteRepository {
    Optional<CommentVote> getCommentVote(Account account, Comment comment, VoteType type);

    long getCommentVoteCount(Comment comment, VoteType type);

    void save(CommentVote commentVote);
}
