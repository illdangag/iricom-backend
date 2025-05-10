package com.illdangag.iricom.core.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.core.data.entity.AccountGroup;
import com.illdangag.iricom.core.data.entity.AccountGroupAccount;
import com.illdangag.iricom.core.data.entity.AccountGroupBoard;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountGroupInfo {
    private String id;

    private String title;

    private String description;

    @JsonProperty("boards")
    @Builder.Default
    private List<BoardInfo> boardInfoList = new ArrayList<>();

    @JsonProperty("accounts")
    @Builder.Default
    private List<AccountInfo> accountInfoList = new ArrayList<>();

    public AccountGroupInfo(AccountGroup accountGroup) {
        this.id = String.valueOf(accountGroup.getId());
        this.title = accountGroup.getTitle();
        this.description = accountGroup.getDescription();

        this.accountInfoList = accountGroup.getAccountGroupAccountList().stream()
                .map(AccountGroupAccount::getAccount)
                .map(AccountInfo::new)
                .collect(Collectors.toList());
        this.boardInfoList = accountGroup.getAccountGroupBoardList().stream()
                .map(AccountGroupBoard::getBoard)
                .map((board) -> {
                    return new BoardInfo(board, null);
                })
                .collect(Collectors.toList());
    }
}
