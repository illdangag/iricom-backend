package com.illdangag.iricom.server.test.data.wrapper;

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
    private boolean undisclosed = false;

    @Builder.Default
    private boolean notificationOnly = false;

    @Builder.Default
    private List<TestAccountInfo> adminList = new LinkedList<>();

    @Builder.Default
    private List<TestAccountInfo> removeAdminList = new LinkedList<>();
}
