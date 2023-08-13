package com.illdangag.iricom.server.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountInfoUpdate {
    @Size(min = 1, max = 20, message = "The nickname must be at least 1 character and less than 20 characters.")
    private String nickname;

    @Size(max = 100, message = "The description must be less than 100 characters.")
    private String description;
}
