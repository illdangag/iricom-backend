package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class BoardAdminInfoCreate {
    @NotNull(message = "The account id is required.")
    private String accountId;

    @NotNull(message = "The board id is required.")
    private String boardId;
}
