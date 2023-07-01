package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.AccountGroup;
import com.illdangag.iricom.server.data.entity.AccountInAccountGroup;
import com.illdangag.iricom.server.data.entity.BoardInAccountGroup;
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
    private List<BoardInfo> boardInfoList = new ArrayList<>();

    @JsonProperty("accounts")
    private List<AccountInfo> accountInfoList = new ArrayList<>();

    public AccountGroupInfo(AccountGroup accountGroup, List<AccountInAccountGroup> accountInAccountGroupList, List<BoardInAccountGroup> boardInAccountGroupList) {
        List<AccountInfo> accountInfoList = accountInAccountGroupList.stream()
                .map(item -> new AccountInfo(item.getAccount()))
                .collect(Collectors.toList());

        List<BoardInfo> boardInfoList = boardInAccountGroupList.stream()
                .map(item -> new BoardInfo(item.getBoard()))
                .collect(Collectors.toList());

        this.id = String.valueOf(accountGroup.getId());
        this.title = accountGroup.getTitle();
        this.description = accountGroup.getDescription();
        this.accountInfoList = accountInfoList;
        this.boardInfoList = boardInfoList;
    }
}
