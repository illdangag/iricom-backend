package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;

@Getter
@Setter
@SuperBuilder
public class SearchRequest {
    @Min(value = 0, message = "Skip must be 0 or greater.")
    @Builder.Default
    protected int skip = 0;

    @Min(value = 1, message = "Limit must be 1 or greater.")
    @Builder.Default
    protected int limit = 20;
}
