package com.illdangag.iricom.server.data.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(indexes = {
})
public class BoardInAccountGroup {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BoardInAccountGroup)) {
            return false;
        }

        BoardInAccountGroup other = (BoardInAccountGroup) object;

        return this.accountGroup.equals(other.accountGroup) && this.board.equals(other.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accountGroup, this.board);
    }
}
