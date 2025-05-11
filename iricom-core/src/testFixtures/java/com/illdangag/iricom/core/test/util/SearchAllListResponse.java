package com.illdangag.iricom.core.test.util;

import com.illdangag.iricom.core.data.request.SearchRequest;

import java.util.List;

@FunctionalInterface
public interface SearchAllListResponse<T> {
    List<T> func(SearchRequest searchRequest);
}
