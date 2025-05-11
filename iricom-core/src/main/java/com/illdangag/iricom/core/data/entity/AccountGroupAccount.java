package com.illdangag.iricom.core.data.entity;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "account_group_account",
        indexes = {
                @Index(name = "AccountInAccountGroup_accountGroup", columnList = "account_group_id"),
        }
)
public class AccountGroupAccount {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AccountGroupAccount)) {
            return false;
        }

        AccountGroupAccount other = (AccountGroupAccount) object;

        return this.accountGroup.equals(other.accountGroup) && this.account.equals(other.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accountGroup, this.account);
    }
}
