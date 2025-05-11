package com.illdangag.iricom.core.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CommentInfoSearch extends SearchRequest {
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
