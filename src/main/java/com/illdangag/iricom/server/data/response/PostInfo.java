package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostContent;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostInfo {
    public enum Type {
        SIMPLE,
        INCLUDE_CONTENT,
    }

    private String id;

    private String type;

    private Long createDate;

    private Long updateDate;

    private String status;

    private String title;

    private String content;

    private Long viewCount;

    private Long upvote;

    private Long downvote;

    private Long commentCount;

    @JsonProperty("account")
    private AccountInfo accountInfo;

    private Boolean isAllowComment;

    public PostInfo(Post post, PostContent postContent, Type type, long commentCount, long upvote, long downvote) {
        this.id = "" + post.getId();
        this.type = postContent.getType().getText();
        this.createDate = DateTimeUtils.getLong(post.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(post.getUpdateDate());
        this.status = postContent.getState().getText();
        this.title = postContent.getTitle();
        this.isAllowComment = postContent.getAllowComment();
        this.viewCount = post.getViewCount();
        this.upvote = upvote;
        this.downvote = downvote;
        this.commentCount = commentCount;

        if (type == Type.INCLUDE_CONTENT) {
            this.content = postContent.getContent();
        }
    }

    public PostInfo(Post post, PostContent postContent, AccountInfo accountInfo, Type type, long commentCount, long upvote, long downvote) {
        this(post, postContent, type, commentCount, upvote, downvote);
        this.accountInfo = accountInfo;
    }
}
