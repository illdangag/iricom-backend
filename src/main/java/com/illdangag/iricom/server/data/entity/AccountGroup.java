package com.illdangag.iricom.server.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(indexes = {
        @Index(name = "AccountGroup_enabled", columnList = "enabled"),
        @Index(name = "AccountGroup_deleted", columnList = "deleted")
})
public class AccountGroup {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @Builder.Default
    private String title = "";

    @Builder.Default
    private String description = "";

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean deleted = false;
}
