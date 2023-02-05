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
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(indexes = {
        @Index(name = "board_createDate", columnList = "createDate"),
        @Index(name = "board_title", columnList = "title"),
})
public class Board {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Size(min = 1, max = 20)
    private String title;

    @Size(max = 100)
    private String description;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Board)) {
            return false;
        }

        Board other = (Board) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

}
