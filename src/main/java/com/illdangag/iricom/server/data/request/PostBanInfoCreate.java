package com.illdangag.iricom.server.data.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@SuperBuilder
public class PostBanInfoCreate {
    @NotNull(message = "Reason is required.")
    @Size(min = 1, max = 1000, message = "Reason must be at least 1 character and less then 1000 characters.")
    private String reason;
}
