package com.illdangag.iricom.server.test.data;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class TestAccountGroupInfo {
    private String title;

    private String description;

    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

    @Builder.Default
    private Boolean deleted = Boolean.FALSE;

    @Builder.Default
    private List<TestAccountInfo> accountList = new ArrayList<>();

    @Builder.Default
    private List<TestBoardInfo> boardList = new ArrayList<>();
}
