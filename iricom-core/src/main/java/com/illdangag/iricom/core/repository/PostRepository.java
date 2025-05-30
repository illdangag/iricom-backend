package com.illdangag.iricom.core.repository;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.entity.Post;
import com.illdangag.iricom.core.data.entity.PostContent;
import com.illdangag.iricom.core.data.entity.type.PostType;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> getPost(long id);

    List<Post> getPublishPostList(Board board, PostType postType, String containTitle, int offset, int limit);

    List<Post> getPublishPostList(Board board, PostType postType, int offset, int limit);

    List<Post> getPublishPostList(Board board, String containTitle, int offset, int limit);

    List<Post> getPublishPostList(Board board, int offset, int limit);

    long getPublishPostCount(Board board, PostType postType, String containTitle);

    long getPublishPostCount(Board board, PostType postType);

    long getPublishPostCount(Board board, String containTitle);

    long getPublishPostCount(Board board);

    List<Post> getPostList(Account account, List<Long> boardIdList, int offset, int limit);

    long getPostCount(Account account, List<Long> boardIdList);

    void save(Post post);

    void save(PostContent postContent);

    void save(Post post, PostContent postContent);
}
