package com.illdangag.iricom.server.data.entity;

import com.illdangag.iricom.server.data.entity.type.AccountPointType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(indexes = {})
public class AccountPointTable {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Builder.Default
    @Column(unique = true)
    private AccountPointType type = AccountPointType.EXTERNAL_POINT;

    @NotNull
    @Builder.Default
    private Long point = 0L;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();
}
