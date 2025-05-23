package com.illdangag.iricom.core.data.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServerInfo {
    private String branch;

    private String commit;

    private String tags;

    private String version;

    private String timestamp;

    private String profile;
}
