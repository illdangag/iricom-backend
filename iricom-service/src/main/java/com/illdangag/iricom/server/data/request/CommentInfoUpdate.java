package com.illdangag.iricom.server.data.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentInfoUpdate {
    @Size(max = 200, message = "The content must be less then 200 characters.")
    private String content;
}
