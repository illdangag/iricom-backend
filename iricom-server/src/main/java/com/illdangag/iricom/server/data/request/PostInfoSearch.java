package com.illdangag.iricom.server.data.request;

import com.illdangag.iricom.server.data.entity.type.PostType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PostInfoSearch extends SearchRequest {
    @Builder.Default
    private PostType type = null;

    @Builder.Default
    private String accountId = "";

    @Builder.Default
    private String title = "";
}
