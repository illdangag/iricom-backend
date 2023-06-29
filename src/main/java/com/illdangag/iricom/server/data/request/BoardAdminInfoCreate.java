package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
public class BoardAdminInfoCreate {
    @NotNull(message = "The account id is required.")
    @Size(min = 1)
    private String accountId;

    @NotNull(message = "The board id is required.")
    @Size(min = 1)
    private String boardId;
}
