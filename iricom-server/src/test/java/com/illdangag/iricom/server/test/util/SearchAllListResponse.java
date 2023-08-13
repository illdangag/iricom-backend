package com.illdangag.iricom.server.test.util;

import com.illdangag.iricom.server.data.request.SearchRequest;

import java.util.List;

@FunctionalInterface
public interface SearchAllListResponse<T> {
    List<T> func(SearchRequest searchRequest);
}
