package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
public class PersonalMessageInfoList extends SearchList {
    @Builder.Default
    @JsonProperty("personalMessages")
    private List<PersonalMessageInfo> personalMessageInfoList = new ArrayList<>();
}
