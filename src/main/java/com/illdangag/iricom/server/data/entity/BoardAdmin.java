package com.illdangag.iricom.server.data.entity;

import com.google.common.base.Objects;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Entity
@Table(indexes = {
        @Index(name = "boardAdmin_createDate", columnList = "createDate"),
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

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean deleted = Boolean.FALSE;

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
