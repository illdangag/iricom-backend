package com.illdangag.iricom.server.data.entity;

import com.google.common.base.Objects;
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
@Table(indexes = {
        @Index(name = "BoardAdmin_createDate", columnList = "createDate"),
        @Index(name = "BoardAdmin_deleted", columnList = "deleted"),
})
public class BoardAdmin {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    private Boolean deleted = false;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BoardAdmin)) {
            return false;
        }

        BoardAdmin other = (BoardAdmin) object;
        if (this.account == null || this.board == null || other.account == null || other.board == null) {
            return false;
        }

        return this.account.equals(other.account) && this.board.equals(other.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.account, this.board);
    }
}
