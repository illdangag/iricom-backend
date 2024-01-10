package com.illdangag.iricom.server.data.entity;

import com.google.common.base.Objects;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Audited
@Table(
        name = "board_admin",
        indexes = {
        }
)
public class BoardAdmin {
    @Id
    @GeneratedValue
    private Long id;

    @NotAudited
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotAudited
    @OneToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

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
