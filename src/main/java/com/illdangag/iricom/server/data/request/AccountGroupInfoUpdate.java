package com.illdangag.iricom.server.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
public class AccountGroupInfoUpdate {
    @NotNull(message = "The title is required.")
    @Size(min = 1, max = 20, message = "The title must be at least 1 character and less then 20 characters.")
    private String title;

    @Size(max = 100, message = "The description must be less then 100 characters.")
    private String description;

    @JsonProperty("accountIds")
    private List<String> accountIdList;

    @JsonProperty("boardIds")
    private List<String> boardIdList;

    private Boolean enabled;
}
