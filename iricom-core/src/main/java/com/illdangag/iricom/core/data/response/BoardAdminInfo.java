package com.illdangag.iricom.core.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.core.data.entity.Board;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class BoardAdminInfo extends BoardInfo {

    public BoardAdminInfo(Board board) {
        super(board, null);
    }

    @JsonProperty("accounts")
    private List<AccountInfo> accountInfoList;
}
