package com.illdangag.iricom.server.data.entity;

import com.illdangag.iricom.server.data.entity.type.VoteType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "comment_vote",
        indexes = {
                @Index(name = "CommentVote_comment", columnList = "comment_id"),
                @Index(name = "CommentVote_account", columnList = "account_id"),
                @Index(name = "CommentVote_type", columnList = "type"),
        }
)
public class CommentVote {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private VoteType type;
}
