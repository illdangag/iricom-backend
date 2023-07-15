package com.illdangag.iricom.server.controller;

import com.illdangag.iricom.server.data.response.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "")
@PropertySource(value = "classpath:/git.properties", ignoreResourceNotFound = true)
public class MainController {

    private final String branch;
    private final String commit;
    private final String tags;
    private final String version;

    @Autowired
    public MainController(@Value("${git.branch:dev}") String branch,
                          @Value("${git.commit.id:}") String commit,
                          @Value("${git.tags:}") String tags,
                          @Value("${git.build.version:}") String version) {
        this.branch = branch;
        this.commit = commit;
        this.tags = tags;
        this.version = version;
    }

    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<ServerInfo> getServerInfo() {

        ServerInfo serverInfo = ServerInfo.builder()
                .branch(branch)
                .commit(commit)
                .tags(tags)
                .version(version)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(serverInfo);
    }
}
