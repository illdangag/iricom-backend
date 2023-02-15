package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostContent;
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

    private String status;

    private String title;

    private String content;

    private Long viewCount;

    private Long upvote;

    private Long downvote;

    @JsonProperty("account")
    private AccountInfo accountInfo;

    private Boolean isAllowComment;

    public PostInfo(Post post, PostContent postContent, Type type) {
        this.id = "" + post.getId();
        this.type = postContent.getType().getText();
        this.status = postContent.getState().getText();
        this.title = postContent.getTitle();
        this.isAllowComment = postContent.getAllowComment();
        this.viewCount = post.getViewCount();
        this.upvote = post.getUpvote();
        this.downvote = post.getDownvote();

        if (type == Type.INCLUDE_CONTENT) {
            this.content = postContent.getContent();
        }
    }

    public PostInfo(Post post, PostContent postContent, AccountInfo accountInfo, Type type) {
        this(post, postContent, type);
        this.accountInfo = accountInfo;
    }
}
