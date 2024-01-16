package com.illdangag.iricom.server.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
        name = "personal_message",
        indexes = {}
)
public class PersonalMessage {
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

    @Size(max = 100)
    @Builder.Default
    private String title = "";

    @Size(max = 1000)
    @Builder.Default
    private String message = "";

    @Builder.Default
    private Boolean receivedConfirm = false;

    @Builder.Default
    private Boolean sendDeleted = false;

    @Builder.Default
    private Boolean receiveDeleted = false;
}
