package com.illdangag.iricom.core.data.request;

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
public class CommentBlockInfoCreate {
    @NotNull(message = "The reason is required.")
    @Size(min = 1, max = 1000, message = "The reason must be at least 1 character and less then 1000 characters.")
    private String reason;
}
