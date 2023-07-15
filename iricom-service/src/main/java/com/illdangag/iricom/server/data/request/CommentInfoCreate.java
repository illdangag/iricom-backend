package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;

@Getter
@Builder
public class CommentInfoCreate {
    private String referenceCommentId;

    @Size(max = 200, message = "The content must be less then 200 characters.")
    private String content;
}
