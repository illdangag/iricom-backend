package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupInfo {
    private String id;

    private String name;

    private String description;

    @JsonProperty("boards")
    private List<BoardInfo> boardInfoList = new ArrayList<>();

    @JsonProperty("accounts")
    private List<AccountInfo> accountInfoList = new ArrayList<>();
}
