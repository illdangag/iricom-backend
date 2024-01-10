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
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "board",
        indexes = {
                @Index(name = "Board_createDate", columnList = "createDate"),
                @Index(name = "Board_title", columnList = "title"),
                @Index(name = "Board_undisclosed", columnList = "undisclosed"),
                @Index(name = "Board_enabled", columnList = "enabled"),
        }
)
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

    @Size(min = 1, max = 50)
    private String title;

    @Size(max = 100)
    private String description;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean notificationOnly = false;

    @Builder.Default
    private Boolean undisclosed = false; // 비공개 게시판 여부

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
