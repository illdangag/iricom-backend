package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
public class BoardAdminInfoSearch {
    @Min(value = 0, message = "Skip must be 0 or greater.")
    @Builder.Default
    private int skip = 0;

    @Min(value = 1, message = "Limit must be 1 or greater.")
    @Builder.Default
    private int limit = 20;

    @NotNull(message = "Keyword is required.")
    @Builder.Default
    private String keyword = "";

    @Builder.Default
    private Boolean enabled = null;
}
