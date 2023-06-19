package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostBan;

import java.util.List;

public interface BanRepository {
    List<PostBan> getPostBanList(Post post);

    long getPostBanCount(Post post);

    void savePostBan(PostBan postBan);

    List<PostBan> getPostBanList(Board board, String reason, int offset, int limit);

    long getPostBanListCount(Board board, String reason);
}
