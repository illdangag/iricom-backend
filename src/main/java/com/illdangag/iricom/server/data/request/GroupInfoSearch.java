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
public class GroupInfoSearch extends SearchRequest {
    @NotNull(message = "Keyword is required.")
    @Builder.Default
    private String keyword = "";
}
