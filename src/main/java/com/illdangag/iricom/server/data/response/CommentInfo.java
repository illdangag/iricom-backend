package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentInfo {
    private String id;

    private String comment;

    private String referenceCommentId;

    private AccountInfo account;

    private Long createDate;

    private Long updateDate;

    private Boolean hasNestedComment;

    private Boolean deleted;

    @JsonProperty("nestedComments")
    private List<CommentInfo> nestedCommentList = null;

    public CommentInfo(Comment comment, AccountInfo accountInfo) {
        this.id = comment.getId().toString();
        if (comment.getReferenceComment() != null) {
            this.referenceCommentId = comment.getReferenceComment().getId().toString();
        }
        this.account = accountInfo;
        this.createDate = DateTimeUtils.getLong(comment.getCreateDate());
        this.updateDate = comment.getUpdateDate() == null ? null : DateTimeUtils.getLong(comment.getUpdateDate());
        this.hasNestedComment = comment.getHasNestedComment();
        this.deleted = comment.getDeleted();
        if (!this.deleted) {
            this.comment = comment.getContent();
        } else {
            this.comment = null;
        }
    }
}
