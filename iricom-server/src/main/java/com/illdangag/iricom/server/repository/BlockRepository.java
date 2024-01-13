package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.CommentBlock;
import com.illdangag.iricom.server.data.entity.PostBlock;

import java.util.List;

public interface BlockRepository {
    List<PostBlock> getPostBlockList(Board board, String reason, int offset, int limit);

    List<PostBlock> getPostBlockList(String reason, int offset, int limit);

    long getPostBlockListCount(String reason);

    long getPostBlockListCount(Board board, String reason);

    List<CommentBlock> getCommentBlockList(Comment comment, Boolean enabled, Integer skip, Integer limit);
}
