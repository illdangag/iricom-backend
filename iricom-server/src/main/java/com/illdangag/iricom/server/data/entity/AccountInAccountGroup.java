package com.illdangag.iricom.server.data.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(indexes = {
        @Index(name = "AccountInAccountGroup_accountGroup", columnList = "account_group_id"),
})
public class AccountInAccountGroup {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AccountInAccountGroup)) {
            return false;
        }

        AccountInAccountGroup other = (AccountInAccountGroup) object;

        return this.accountGroup.equals(other.accountGroup) && this.account.equals(other.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accountGroup, this.account);
    }
}
