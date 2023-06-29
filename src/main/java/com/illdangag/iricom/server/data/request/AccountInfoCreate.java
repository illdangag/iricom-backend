package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
public class AccountInfoCreate {
    @Builder.Default
    private String email = "";

    @NotNull(message = "The nickname is required.")
    @Size(min = 1, max = 20, message = "The nickname must be at least 1 character and less than 20 characters.")
    private String nickname;

    @Builder.Default
    @Size(max = 100, message = "The description must be less than 100 characters.")
    private String description = "";
}
