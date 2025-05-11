package com.illdangag.iricom.core.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class SearchList {
    protected long total;

    protected long skip;

    protected long limit;
}
