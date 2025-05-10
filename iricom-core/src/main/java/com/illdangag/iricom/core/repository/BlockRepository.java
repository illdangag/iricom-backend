package com.illdangag.iricom.core.repository;

import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.entity.Comment;
import com.illdangag.iricom.core.data.entity.CommentBlock;
import com.illdangag.iricom.core.data.entity.PostBlock;

import java.util.List;

public interface BlockRepository {
    List<PostBlock> getPostBlockList(Board board, String reason, int offset, int limit);

    List<PostBlock> getPostBlockList(String reason, int offset, int limit);

    long getPostBlockListCount(String reason);

    long getPostBlockListCount(Board board, String reason);

    List<CommentBlock> getCommentBlockList(Comment comment, Integer skip, Integer limit);

    void remove(PostBlock postBlock);

    void remove(CommentBlock commentBlock);
}
