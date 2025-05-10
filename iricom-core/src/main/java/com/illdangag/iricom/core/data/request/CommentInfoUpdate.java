package com.illdangag.iricom.core.data.request;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CommentInfoUpdate {
    @Size(min = 1, max = 200, message = "The content must be less then 200 characters.")
    private String content;
}
