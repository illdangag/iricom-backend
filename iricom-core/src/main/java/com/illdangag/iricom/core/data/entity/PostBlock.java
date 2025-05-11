package com.illdangag.iricom.core.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "post_block",
        indexes = {
                @Index(name = "PostBlock_createDate", columnList = "createDate"),
                @Index(name = "PostBlock_reason", columnList = "reason"),
                @Index(name = "PostBlock_post", columnList = "post_id"),
                @Index(name = "PostBlock_adminAccount", columnList = "admin_account_id")
        }
)
@Audited(withModifiedFlag = true)
public class PostBlock {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "admin_account_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Account adminAccount;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @Builder.Default
    @Size(max = 1000)
    private String reason = "";
}
