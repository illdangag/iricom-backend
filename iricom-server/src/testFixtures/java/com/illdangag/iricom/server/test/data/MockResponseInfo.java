package com.illdangag.iricom.server.test.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class MockResponseInfo {
    private int statusCode;

    private String responseBody;

    private Map<String, String> headerMap = new HashMap<>();

    public MockResponseInfo(MvcResult mvcResult) {
        MockHttpServletResponse response = mvcResult.getResponse();
        this.statusCode = response.getStatus();
        try {
            this.responseBody = response.getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception exception) {
            log.error("Fail to parse response.");
            this.responseBody = "";
        }
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            this.headerMap.put(headerName, response.getHeader(headerName));
        }
    }

    @Override
    public String toString() {
        List<String> logList = new LinkedList<>();
        logList.add("Status code: " + this.statusCode);
        logList.add("Header");
        if (!headerMap.isEmpty()) {
            Set<String> headerNameSet = this.headerMap.keySet();
            for (String headerName : headerNameSet) {
                logList.add("\t" + headerName + ": " + this.headerMap.get(headerName));
            }
        }
        logList.add("Response body");
        logList.add(this.responseBody);
        return String.join("\n", logList);
    }
}
