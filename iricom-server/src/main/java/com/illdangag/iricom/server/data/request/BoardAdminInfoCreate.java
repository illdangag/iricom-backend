package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class BoardAdminInfoCreate {
    @NotBlank(message = "The account id is required.")
    private String accountId;

    @NotBlank(message = "The board id is required.")
    private String boardId;
}
