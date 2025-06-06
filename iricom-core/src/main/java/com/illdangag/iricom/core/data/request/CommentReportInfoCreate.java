package com.illdangag.iricom.core.data.request;

import com.illdangag.iricom.core.data.entity.type.ReportType;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentReportInfoCreate {
    @NotNull(message = "The type is required.")
    private ReportType type;

    @Builder.Default
    @Size(max = 10000, message = "The reason must be less then 10000 characters.")
    private String reason = "";
}
