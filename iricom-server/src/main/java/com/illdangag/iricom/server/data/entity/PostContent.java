package com.illdangag.iricom.server.data.entity;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder(builderMethodName = "innerBuilder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "post_content",
        indexes = {
                @Index(name = "PostContent_title", columnList = "title"),
                @Index(name = "PostContent_type", columnList = "type"),
                @Index(name = "PostContent_titleAndType", columnList = "title,type"),
        }
)
public class PostContent {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Builder.Default
    private String title = "";

    @Builder.Default
    @Size(max = 10000)
    private String content = "";

    @Enumerated(EnumType.STRING)
    private PostState state;

    @Builder.Default
    private Boolean allowComment = true;

    public static PostContentBuilder builder(Post post, PostState state) {
        return innerBuilder().post(post).state(state);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PostContent)) {
            return false;
        }
        PostContent other = (PostContent) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
