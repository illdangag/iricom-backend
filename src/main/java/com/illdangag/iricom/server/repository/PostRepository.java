package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostContent;
import com.illdangag.iricom.server.data.entity.PostType;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> getPost(long id);

    List<Post> getPublishPostList(Board board, PostType postType, int offset, int limit);

    long getPublishPostCount(Board board, PostType postType);

    List<Post> getPublishPostList(Board board, PostType postType, String containTitle, int offset, int limit);

    long getPublishPostCount(Board board, PostType postType, String containTitle);

    void save(Post post);

    void save(PostContent postContent);

    void save(Post post, PostContent postContent);
}
