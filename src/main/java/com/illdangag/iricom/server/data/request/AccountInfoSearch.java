package com.illdangag.iricom.server.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@ToString
public class AccountInfoSearch {
    @Min(value = 0, message = "Skip must be 0 or greater.")
    @Builder.Default
    private int skip = 0;

    @Min(value = 1, message = "Limit must be 1 or greater.")
    @Builder.Default
    private int limit = 20;

    @NotNull(message = "Keyword is required.")
    @Builder.Default
    private String keyword = "";

    @Column(name = "is_block")
    @Builder.Default
    private Boolean block = null;

    public Pageable getPageable() {
        Sort sort = Sort.by(
                Sort.Order.asc("email")
        );
        return PageRequest.of(skip / limit, limit, sort);
    }
}
