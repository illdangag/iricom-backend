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
@ToString
@Entity
@Table(indexes = {
        @Index(name = "Board_createDate", columnList = "createDate"),
        @Index(name = "Board_title", columnList = "title"),
        @Index(name = "Board_undisclosed", columnList = "undisclosed"),
        @Index(name = "Board_enabled", columnList = "enabled"),
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
    private Boolean enabled = true;

    @Size(min = 1, max = 50)
    private String title;

    @Size(max = 100)
    private String description;

    @Builder.Default
    private Boolean undisclosed = false; // 공개되지 않은 게시판

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
