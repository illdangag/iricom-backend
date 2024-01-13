package com.illdangag.iricom.server.data.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalMessageInfoCreate {
    @NotBlank(message = "The title is required.")
    @Size(min = 1, max = 100, message = "The title must be at least 1 character and less then 100 characters.")
    private String title;

    @NotBlank(message = "The message is required.")
    @Size(min = 1, max = 1000, message = "The message must be at least 1 character and less then 100 characters.")
    private String message;

    @NotBlank(message = "The receiverAccountId is required.")
    private String receiverAccountId;
}
