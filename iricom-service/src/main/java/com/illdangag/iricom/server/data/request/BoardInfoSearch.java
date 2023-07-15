package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@SuperBuilder
@ToString
public class BoardInfoSearch extends SearchRequest {
    @NotNull(message = "The keyword is required.")
    @Builder.Default
    private String keyword = "";

    @Builder.Default
    private Boolean enabled = null;
}
