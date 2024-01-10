package com.illdangag.iricom.server.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
        name = "comment",
        indexes = {
                @Index(name = "Comment_createDate", columnList = "createDate"),
        }
)
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder.Default
    @ManyToOne
    @JoinColumn(name = "reference_comment_id")
    private Comment referenceComment = null;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updateDate = null;

    @Builder.Default
    private String content = "";

    @Builder.Default
    private Boolean deleted = false;

    @Builder.Default
    private Boolean hasNestedComment = false;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Comment)) {
            return false;
        }

        Comment other = (Comment) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
