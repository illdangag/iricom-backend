package com.illdangag.iricom.core.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PostBlockInfoSearch extends SearchRequest {
    @Builder.Default
    private String reason = "";
}
