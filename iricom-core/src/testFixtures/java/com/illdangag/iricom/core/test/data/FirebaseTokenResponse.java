package com.illdangag.iricom.core.test.data;

import lombok.Getter;

@Getter
public class FirebaseTokenResponse {
    private String kind;

    private String localId;

    private String email;

    private String displayName;

    private String idToken;

    private boolean registered;

    private String refreshToken;

    private String expiresIn;
}
