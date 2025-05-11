package com.illdangag.iricom.core.data.entity;

import com.google.common.base.Objects;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "post",
        indexes = {
                @Index(name = "Post_createDate", columnList = "createDate"),
        }
)
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id")
    private PostContent content;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "temporary_content_id")
    private PostContent temporaryContent;

    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Boolean deleted = false;

    @Builder.Default
    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY)
    PostBlock postBlock = null;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Post)) {
            return false;
        }
        Post other = (Post) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public boolean isPublish() {
        return this.content != null;
    }
}
