package com.illdangag.iricom.storage.controller;

import com.illdangag.iricom.server.data.response.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/storage")
@PropertySources({
        @PropertySource(value = "classpath:storage-git.properties", ignoreResourceNotFound = false),
        @PropertySource(value = "classpath:storage-version.properties", ignoreResourceNotFound = false),
})
public class StorageMainController {
    private final String branch;
    private final String commit;
    private final String tags;
    private final String version;
    private final String timestamp;
    private final String profile;

    @Autowired
    public StorageMainController(@Value("${git.branch:dev}") String branch,
                                 @Value("${git.commit.id:}") String commit,
                                 @Value("${git.tags:}") String tags,
                                 @Value("${storage.version:}") String version,
                                 @Value("${storage.timestamp:}") String timestamp,
                                 @Value("${server.profile:}") String profile) {
        this.branch = branch;
        this.commit = commit;
        this.tags = tags;
        this.version = version;
        this.timestamp = timestamp;
        this.profile = profile;
    }

    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<ServerInfo> getServerInfo() {

        ServerInfo serverInfo = ServerInfo.builder()
                .branch(branch)
                .commit(commit)
                .tags(tags)
                .version(version)
                .timestamp(timestamp)
                .profile(profile)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(serverInfo);
    }
}
