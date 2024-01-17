package com.illdangag.iricom.server.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestPersonalMessageInfo {
    private TestAccountInfo sender;

    private TestAccountInfo receiver;

    private String title;

    private String message;

    @Builder.Default
    private boolean sendDeleted = false;

    @Builder.Default
    private boolean receiveDeleted = false;
}
