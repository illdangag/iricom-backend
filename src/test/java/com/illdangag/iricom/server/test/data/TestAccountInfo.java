package com.illdangag.iricom.server.test.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestAccountInfo {
    private String email;

    @Builder.Default
    private boolean isAdmin = false;

    private String nickname;

    private String description;

    @Builder.Default
    private boolean isUnregistered = false;
}
