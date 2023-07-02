package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.*;
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

    public AccountGroupInfo(AccountGroup accountGroup, List<Account> accountList, List<Board> boardList) {
        this.id = String.valueOf(accountGroup.getId());
        this.title = accountGroup.getTitle();
        this.description = accountGroup.getDescription();

        this.accountInfoList = accountList.stream()
                .map(AccountInfo::new)
                .collect(Collectors.toList());
        this.boardInfoList = boardList.stream()
                .map(BoardInfo::new)
                .collect(Collectors.toList());
    }
}
