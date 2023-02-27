package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> getComment(long id);

    List<Comment> getCommentList(Post post, int offset, int limit);

    long getCommentCount(Post post);

    List<Comment> getCommentList(Post post, Comment referenceComment, int offset, int limit);

    long getCommentListSize(Post post, Comment referenceComment);

    long getCommentListSize(Post post);

    void save(Comment comment);
}
