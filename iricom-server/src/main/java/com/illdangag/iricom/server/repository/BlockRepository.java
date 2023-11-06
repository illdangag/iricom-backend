package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface BlockRepository {
    List<PostBlock> getPostBlockList(Post post);

    long getPostBlockCount(Post post);

    void save(PostBlock postBlock);

    List<PostBlock> getPostBlockList(String reason, int offset, int limit);

    long getPostBlockListCount(String reason);

    List<PostBlock> getPostBlockList(Board board, String reason, int offset, int limit);

    long getPostBlockListCount(Board board, String reason);

    Optional<PostBlock> getPostBlock(String id);

    Optional<PostBlock> getPostBlock(long id);

    Optional<PostBlock> getPostBlock(Post post);

    Optional<CommentBlock> getCommentBlock(long id);

    List<CommentBlock> getCommentBlockList(Comment comment, Boolean enabled, Integer skip, Integer limit);

    void save(CommentBlock commentBlock);
}
