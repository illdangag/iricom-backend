package com.illdangag.iricom.core.data.request;

import com.illdangag.iricom.core.data.entity.type.PostType;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostInfoCreate {
    @NotNull(message = "The title is required.")
    @Size(min = 1, max = 40, message = "The title must be at least 1 character and less than 40 characters.")
    private String title;

    @NotNull(message = "The type is required.")
    private PostType type;

    @Builder.Default
    @Size(max = 10000, message = "The content must be less than 10000 characters.")
    private String content = "";

    @Builder.Default
    private Boolean allowComment = true;
}
