package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostVote;
import com.illdangag.iricom.server.data.entity.type.VoteType;

import java.util.Optional;

public interface PostVoteRepository {
    Optional<PostVote> getPostVote(Account account, Post post, VoteType type);

    long getPostVoteCount(Post post, VoteType voteType);

    void save(PostVote postVote);
}
