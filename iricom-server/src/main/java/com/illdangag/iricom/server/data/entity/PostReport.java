package com.illdangag.iricom.server.data.entity;

import com.illdangag.iricom.server.data.entity.type.ReportType;
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
        @Index(name = "PostReport_account_and_post", columnList = "report_account_id,post_id"),
        @Index(name = "PostReport_enabled", columnList = "enabled"),
        @Index(name = "PostReport_reason", columnList = "reason"),
        @Index(name = "PostReport_createDate", columnList = "createDate"),
})
public class PostReport {
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
    private Account account; // 신고자 계정

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post; // 신고 대상 게시물

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReportType type = ReportType.ETC;

    @Builder.Default
    @Size(max = 10000)
    private String reason = "";

    @Builder.Default
    private Boolean enabled = true;
}
