package com.illdangag.iricom.core.repository;

import com.illdangag.iricom.core.data.entity.FirebaseAuthentication;

import java.util.Optional;

public interface FirebaseAuthenticationRepository {
    Optional<FirebaseAuthentication> getFirebaseAuthentication(String id);

    void save(FirebaseAuthentication firebaseAuthentication);
}
