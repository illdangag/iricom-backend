package com.illdangag.iricom.core.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardInfoCreate {
    @NotBlank(message = "The title is required.")
    @Size(min = 1, max = 50, message = "The title must be at least 1 character and less than 50 characters.")
    private String title;

    @Size(max = 100, message = "The description must be less than 100 characters.")
    @Builder.Default
    private String description = "";

    @Builder.Default
    private Boolean enabled = Boolean.TRUE; // 활성화

    @Builder.Default
    private Boolean undisclosed = Boolean.FALSE; // 비공개 게시판

    @Builder.Default
    private Boolean notificationOnly = Boolean.FALSE; // 공지 사항 전용
}
