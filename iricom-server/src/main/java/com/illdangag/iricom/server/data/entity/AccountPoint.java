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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "account_point",
        indexes = {
            @Index(name = "AccountPoint_account_id", columnList = "account_id")
        }
)
public class AccountPoint {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @Builder.Default
    private AccountPointType type = AccountPointType.EXTERNAL_POINT;

    @NotNull
    @Builder.Default
    private Long point = 0L;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();
}
