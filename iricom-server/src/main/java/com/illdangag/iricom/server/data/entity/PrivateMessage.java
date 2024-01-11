package com.illdangag.iricom.server.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "private_message",
        indexes = {}
)
public class PrivateMessage {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "send_account_id")
    private Account sendAccount;

    @ManyToOne
    @JoinColumn(name = "receive_account_id")
    private Account receiveAccount;

    @Builder.Default
    private Boolean deleted = false;
}
