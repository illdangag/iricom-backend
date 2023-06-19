package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
public class PostBanCreate {
    @NotNull(message = "Reason is requried.")
    @Size(min = 1, max = 1000, message = "Reason must be at least 1 character and less then 1000 characters.")
    private String reason;
}
