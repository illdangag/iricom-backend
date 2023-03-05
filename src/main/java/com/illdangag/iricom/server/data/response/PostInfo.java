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
    /**
     * 내용 포함 여부
     */
    public enum ResponseType {
        NOT_INCLUDE_CONTENT,
        INCLUDE_CONTENT,
    }

    private String id;

    private Long createDate;

    private Long updateDate;

    @JsonProperty("account")
    private AccountInfo accountInfo;

    private String status;

    private String type;

    private String title;

    private String content;

    private Boolean isAllowComment;

    private Long viewCount;

    private Long upvote;

    private Long downvote;

    private Long commentCount;

    private Boolean isPublish;

    private Boolean hasTemporary;

    private String boardId;

    public PostInfo(Post post, PostContent postContent, ResponseType responseType, long commentCount, long upvote, long downvote) {
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
        this.isPublish = post.getContent() != null;
        this.hasTemporary = post.getTemporaryContent() != null;
        this.boardId = "" + post.getBoard().getId();

        if (responseType == ResponseType.INCLUDE_CONTENT) {
            this.content = postContent.getContent();
        }
    }

    public PostInfo(Post post, PostContent postContent, AccountInfo accountInfo, ResponseType responseType, long commentCount, long upvote, long downvote) {
        this(post, postContent, responseType, commentCount, upvote, downvote);
        this.accountInfo = accountInfo;
    }
}
