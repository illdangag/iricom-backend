package com.illdangag.iricom.server.data.entity;

import com.google.common.base.Objects;
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
        @Index(name = "Account_email", columnList = "email"),
})
public class Account {
    @Id
    @GeneratedValue
    @Builder.Default
    private Long id = null;

    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime lastActivityDate = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "account_detail_id")
    private AccountDetail accountDetail;

    @Builder.Default
    private boolean deleted = false;

    private String email;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "auth")
    private AccountAuth auth = AccountAuth.UNREGISTERED_ACCOUNT;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) {
            return false;
        }

        Account other = (Account) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
