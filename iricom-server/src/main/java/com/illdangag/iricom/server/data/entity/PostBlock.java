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
@Entity
@Table(
        name = "post_block",
        indexes = {
                @Index(name = "PostBlock_createDate", columnList = "createDate"),
                @Index(name = "PostBlock_reason", columnList = "reason"),
                @Index(name = "PostBlock_post", columnList = "post_id"),
                @Index(name = "PostBlock_adminAccount", columnList = "admin_account_id"),
                @Index(name = "PostBlock_enabled", columnList = "enabled"),
        }
)
public class PostBlock {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "admin_account_id")
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

    @Builder.Default
    private Boolean enabled = true;
}
