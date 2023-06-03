package com.illdangag.iricom.server.data.request;

import com.illdangag.iricom.server.data.entity.ReportType;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostReportCreate {
    @NotNull(message = "Board id is required.")
    private String boardId;

    @NotNull(message = "Post id is required.")
    private String postId;

    @NotNull(message = "Type is required.")
    private ReportType type;

    @Builder.Default
    @Size(max = 10000, message = "The reason must be less then 10000 characters.")
    private String reason = "";
}
