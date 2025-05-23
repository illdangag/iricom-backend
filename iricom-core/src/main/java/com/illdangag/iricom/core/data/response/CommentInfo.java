package com.illdangag.iricom.core.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.core.data.entity.Comment;
import com.illdangag.iricom.core.util.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentInfo {
    private String id;

    private String content;

    private String referenceCommentId;

    private Long createDate;

    private Long updateDate;

    private Long upvote;

    private Long downvote;

    private Boolean hasNestedComment;

    private Boolean deleted;

    private AccountInfo account;

    @JsonProperty("nestedComments")
    private List<CommentInfo> nestedCommentList = null;

    private Boolean report;

    public CommentInfo(Comment comment, AccountInfo accountInfo, long upvote, long downvote, long reportCount) {
        this.id = comment.getId().toString();

        if (comment.getReferenceComment() != null) {
            this.referenceCommentId = comment.getReferenceComment().getId().toString();
        }

        this.createDate = DateTimeUtils.getLong(comment.getCreateDate());
        this.updateDate = comment.getUpdateDate() == null ? this.createDate : DateTimeUtils.getLong(comment.getUpdateDate());
        this.upvote = upvote;
        this.downvote = downvote;
        this.hasNestedComment = comment.getHasNestedComment();
        this.deleted = comment.getDeleted();
        this.account = accountInfo;

        if (!this.deleted) {
            this.content = comment.getContent();
        } else {
            this.content = null;
        }

        this.report = reportCount >= 10;

        if (this.report) {
            this.content = null;
        }
    }
}
