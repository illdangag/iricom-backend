package com.illdangag.iricom.server.test.data;

import lombok.Builder;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Builder
public class TestBoardInfo {
    @Builder.Default
    private String title = "";

    @Builder.Default
    private String description = "";

    @Builder.Default
    private boolean isEnabled = true;

    @Builder.Default
    private List<TestAccountInfo> adminList = new LinkedList<>();
}
