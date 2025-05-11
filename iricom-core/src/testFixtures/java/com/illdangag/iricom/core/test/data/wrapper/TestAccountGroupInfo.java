package com.illdangag.iricom.core.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class TestAccountGroupInfo {
    @Setter
    private String id;

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
