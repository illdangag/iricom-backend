package com.illdangag.iricom.server.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardInfoCreate {
    @NotNull(message = "Title is required.")
    @Size(min = 1, max = 20, message = "The title must be at least 1 character and less than 20 characters.")
    private String title;

    @Size(max = 100, message = "The description must be less than 100 characters.")
    @Builder.Default
    private String description = "";

    @Builder.Default
    private Boolean enabled = Boolean.TRUE;
}
