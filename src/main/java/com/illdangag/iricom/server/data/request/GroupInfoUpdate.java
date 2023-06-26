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
public class GroupInfoUpdate {
    @Size(min = 1, max = 20, message = "Title must be at least 1 character and less than 20 characters.")
    private String title;

    @Size(max = 100, message = "Description must be less than 100 characters.")
    private String description;

    private Boolean enabled;
}
