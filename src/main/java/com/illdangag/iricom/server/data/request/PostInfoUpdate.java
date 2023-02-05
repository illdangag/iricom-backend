package com.illdangag.iricom.server.data.request;

import com.illdangag.iricom.server.data.entity.PostType;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostInfoUpdate {
    @Size(min = 1, max = 40, message = "The title must be at least 1 character and less than 40 characters.")
    private String title;

    private PostType type;

    @Size(max = 10000, message = "The content must be less than 10000 characters.")
    private String content;

    private Boolean isAllowComment;
}
