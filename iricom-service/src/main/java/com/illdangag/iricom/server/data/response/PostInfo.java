package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostContent;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    @JsonProperty("account")
    private AccountInfo accountInfo;

    private String status;

    private String type;

    private String title;

    private String content;

    private Boolean allowComment;

    private Long viewCount;

    private Long upvote;

    private Long downvote;

    private Long commentCount;

    private Boolean publish;

    private Boolean hasTemporary;

    private String boardId;

    private Boolean report;

    private Boolean ban;

    public PostInfo(Post post, boolean includeContent, PostState postState, long commentCount, long upvote, long downvote, long reportCount, boolean isBan) {
        this.id = String.valueOf(post.getId());
        this.createDate = DateTimeUtils.getLong(post.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(post.getUpdateDate());
        this.accountInfo = new AccountInfo(post.getAccount());
        this.viewCount = post.getViewCount();
        this.upvote = upvote;
        this.downvote = downvote;
        this.commentCount = commentCount;
        this.publish = post.getContent() != null;
        this.hasTemporary = post.getTemporaryContent() != null;
        this.boardId = String.valueOf(post.getBoard().getId());

        PostContent content;
        if (postState == PostState.PUBLISH) {
            if (post.getContent() == null) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_PUBLISH_CONTENT);
            }
            content = post.getContent();
        } else {
            // 임시 저장 내용을 우선
            if (post.getTemporaryContent() != null) {
                content = post.getTemporaryContent();
            } else {
                content = post.getContent();
            }
        }
        this.type = content.getType().getText();
        this.status = content.getState().getText();
        this.title = content.getTitle();
        this.allowComment = content.getAllowComment();

        if (includeContent) {
            this.content = content.getContent();
        }

        this.report = reportCount >= 10;
        this.ban = isBan;

        if (this.report || this.ban) {
            this.content = null;
        }
    }
}
