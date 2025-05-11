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
        name = "comment_block",
        indexes = {
                @Index(name = "CommentBlock_Comment", columnList = "comment_id"),
        }
)
@Audited(withModifiedFlag = true)
public class CommentBlock {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "comment_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Comment comment;

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
