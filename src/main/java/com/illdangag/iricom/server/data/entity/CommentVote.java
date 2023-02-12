package com.illdangag.iricom.server.data.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "comment_vote_comment_id", columnList = "comment_id"),
        @Index(name = "comment_vote_account_id", columnList = "account_id"),
        @Index(name = "comment_vote_type", columnList = "type")
})
public class CommentVote {
    @Id
    @GeneratedValue
    private Long id;

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
