package com.illdangag.iricom.server.data.entity;

import com.illdangag.iricom.server.data.entity.type.VoteType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "post_vote",
        indexes = {
                @Index(name = "PostVote_post", columnList = "post_id"),
                @Index(name = "PostVote_account", columnList = "account_id"),
                @Index(name = "PostVote_type", columnList = "type")
        }
)
public class PostVote {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private VoteType type;
}
