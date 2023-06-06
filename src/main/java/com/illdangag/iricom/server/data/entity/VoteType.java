package com.illdangag.iricom.server.data.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum VoteType {
    UPVOTE("upvote"),
    DOWNVOTE("downvote");

    private String text;

    VoteType(String text) {
        this.text = text;
    }

    @JsonCreator
    public static VoteType setValue(String key) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> new IricomException(IricomErrorCode.INVALID_REQUEST, "Type is invalid. (upvote, downvote)"));
    }
}
