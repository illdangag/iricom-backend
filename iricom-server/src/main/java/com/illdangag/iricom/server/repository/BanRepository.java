package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostBan;

import java.util.List;
import java.util.Optional;

public interface BanRepository {
    List<PostBan> getPostBanList(Post post);

    long getPostBanCount(Post post);

    void savePostBan(PostBan postBan);

    List<PostBan> getPostBanList(String reason, int offset, int limit);

    long getPostBanListCount(String reason);

    List<PostBan> getPostBanList(Board board, String reason, int offset, int limit);

    long getPostBanListCount(Board board, String reason);

    Optional<PostBan> getPostBan(String id);

    Optional<PostBan> getPostBan(long id);

    Optional<PostBan> getPostBan(Post post);
}
