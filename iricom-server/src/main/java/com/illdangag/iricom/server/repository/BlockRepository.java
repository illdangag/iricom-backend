package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface BlockRepository {
    List<PostBlock> getPostBlockList(Post post);

    List<PostBlock> getPostBlockList(String reason, int offset, int limit);

    long getPostBlockListCount(String reason);

    List<PostBlock> getPostBlockList(Board board, String reason, int offset, int limit);

    long getPostBlockListCount(Board board, String reason);

    List<CommentBlock> getCommentBlockList(Comment comment, Boolean enabled, Integer skip, Integer limit);
}
