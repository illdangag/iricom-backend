package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
public class CommentInfoSearch {
    @Min(value = 0, message = "Skip must be 0 or greater.")
    @Builder.Default
    private int skip = 0;

    @Min(value = 1, message = "Limit must be 1 or greater.")
    @Builder.Default
    private int limit = 20;

    /**
     * 대댓글 포함 여부
     */
    @Builder.Default
    public boolean includeComment = false;

    /**
     * 대댓글을 포함 하는 경우 최대 갯수
     */
    @Builder.Default
    public int includeCommentLimit = 5;

    /**
     * referenceCommentId가 설정된 경우 해당 댓글를 기준으로 대댓글을 조회
     */
    @Builder.Default
    public String referenceCommentId = "";
}
