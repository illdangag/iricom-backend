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
        name = "account_group_board",
        indexes = {
                @Index(name = "BoardInAccountGroup_accountGroup", columnList = "account_group_id"),
        }
)
public class AccountGroupBoard {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @ManyToOne
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "board_id")
    private Board board;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AccountGroupBoard)) {
            return false;
        }

        AccountGroupBoard other = (AccountGroupBoard) object;

        return this.accountGroup.equals(other.accountGroup) && this.board.equals(other.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accountGroup, this.board);
    }
}
