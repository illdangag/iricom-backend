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
@Table(indexes = {
        @Index(name = "PostBan_createDate", columnList = "createDate"),
        @Index(name = "PostBan_post", columnList = "post_id"),
        @Index(name = "PostBan_adminAccount", columnList = "admin_account_id"),
        @Index(name = "PostBan_enabled", columnList = "enabled"),
})
public class PostBan {
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
