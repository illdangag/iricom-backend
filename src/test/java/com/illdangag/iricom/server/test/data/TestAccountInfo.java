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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TestAccountInfo)) {
            return false;
        }

        TestAccountInfo other = (TestAccountInfo) object;
        return this.email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return this.email.hashCode();
    }
}
