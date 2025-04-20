package com.illdangag.iricom.server.data.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.Getter;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum PersonalMessageStatus {
    ALL("all"),
    UNREAD("unread");

    private String text;

    PersonalMessageStatus(String text) {
        this.text = text;
    }

    @JsonCreator
    public static PersonalMessageStatus setValue(String text) {
        for (PersonalMessageStatus status : PersonalMessageStatus.values()) {
            if (status.text.equalsIgnoreCase(text)) {
                return status;
            }
        }

        String itemJoining = Arrays.stream(PersonalMessageStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining());

        throw new IricomException(IricomErrorCode.INVALID_REQUEST, "State is invalid. (" + itemJoining + ")");
    }

    public static class PersonalMessageStatusConverter implements Converter<String, PersonalMessageStatus> {
        @Override
        public PersonalMessageStatus convert(String status) {
            return PersonalMessageStatus.setValue(status);
        }
    }
}
