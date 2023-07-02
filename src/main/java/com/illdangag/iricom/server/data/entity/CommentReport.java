package com.illdangag.iricom.server.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(indexes = {
        @Index(name = "CommentReport_account_and_comment", columnList = "report_account_id,comment_id"),
        @Index(name = "CommentReport_enabled", columnList = "enabled"),
})
public class CommentReport {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "report_account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReportType type = ReportType.ETC;

    @Builder.Default
    @Size(max = 10000)
    private String reason = "";

    @Builder.Default
    private Boolean enabled = true;
}
