package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.FirebaseAuthentication;

import java.util.Optional;

public interface FirebaseAuthenticationRepository {
    Optional<FirebaseAuthentication> getFirebaseAuthentication(String id);

    void save(FirebaseAuthentication firebaseAuthentication);
}
