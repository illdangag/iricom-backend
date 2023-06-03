package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> getPost(long id);

    Optional<Post> getPost(String id);

    List<Post> getPublishPostList(Board board, PostType postType, String containTitle, int offset, int limit);

    List<Post> getPublishPostList(Board board, PostType postType, int offset, int limit);

    List<Post> getPublishPostList(Board board, String containTitle, int offset, int limit);

    List<Post> getPublishPostList(Board board, int offset, int limit);

    long getPublishPostCount(Board board, PostType postType, String containTitle);

    long getPublishPostCount(Board board, PostType postType);

    long getPublishPostCount(Board board, String containTitle);

    long getPublishPostCount(Board board);

    List<Post> getPostList(Account account, int offset, int limit);

    long getPostCount(Account account);

    void save(Post post);

    void save(PostContent postContent);

    void save(Post post, PostContent postContent);
}
