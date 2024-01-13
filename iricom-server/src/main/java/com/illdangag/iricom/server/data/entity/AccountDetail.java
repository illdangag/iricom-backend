package com.illdangag.iricom.server.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "account_detail",
        indexes = {
                @Index(name = "AccountDetail_nickname", columnList = "nickname"),
                @Index(name = "AccountDetail_createDate", columnList = "createDate"),
        }
)
public class AccountDetail {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @Size(max = 20)
    @Builder.Default
    private String nickname = "";

    @Size(max = 100)
    @Builder.Default
    private String description = "";

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AccountDetail)) {
            return false;
        }

        AccountDetail other = (AccountDetail) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
