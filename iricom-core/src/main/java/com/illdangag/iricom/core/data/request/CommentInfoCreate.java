package com.illdangag.iricom.core.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
public class CommentInfoCreate {
    private String referenceCommentId;

    @NotNull
    @Size(min = 1, max = 200, message = "The content must be less then 200 characters.")
    private String content;
}
