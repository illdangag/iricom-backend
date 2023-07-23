package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;

@Getter
@SuperBuilder
@ToString
public class AccountInfoSearch extends SearchRequest {
    @NotNull(message = "The keyword is required.")
    @Builder.Default
    private String keyword = "";

    @Builder.Default
    private Boolean blocked = null;

    public Pageable getPageable() {
        Sort sort = Sort.by(
                Sort.Order.asc("email")
        );
        return PageRequest.of(skip / limit, limit, sort);
    }
}
