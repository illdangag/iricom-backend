package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.illdangag.iricom.server.data.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardInfo {
    private String id;

    private String title;

    private String description;

    private Boolean enabled;

    private Boolean unDisclosed;

    private Boolean notificationOnly;

    public BoardInfo(Board board) {
        this.id = String.valueOf(board.getId());
        this.title = board.getTitle();
        this.description = board.getDescription();
        this.enabled = board.getEnabled();
        this.unDisclosed = board.getUndisclosed();
        this.notificationOnly = board.getNotificationOnly();
    }
}

