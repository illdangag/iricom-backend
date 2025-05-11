package com.illdangag.iricom.core.data.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;

@Getter
@Builder
public class BoardInfoUpdate {
    @Size(min = 1, max = 20, message = "The title must be at least 1 character and less than 20 characters.")
    private String title;

    @Size(max = 100, message = "The description must be less than 100 characters.")
    private String description;

    private Boolean enabled;

    private Boolean notificationOnly;

    private Boolean undisclosed;
}
