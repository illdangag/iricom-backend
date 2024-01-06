package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.FirebaseAuthentication;
import com.illdangag.iricom.server.repository.FirebaseAuthenticationRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class FirebaseAuthenticationRepositoryImpl implements FirebaseAuthenticationRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<FirebaseAuthentication> getFirebaseAuthentication(String id) {
        final String jpql = "SELECT fa FROM FirebaseAuthentication fa" +
                " WHERE fa.id = :id";

        TypedQuery<FirebaseAuthentication> query = this.entityManager.createQuery(jpql, FirebaseAuthentication.class);
        query.setParameter("id", id);
        List<FirebaseAuthentication> firebaseAuthenticationList = query.getResultList();

        if (firebaseAuthenticationList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(firebaseAuthenticationList.get(0));
        }
    }

    @Override
    public void save(FirebaseAuthentication firebaseAuthentication) {
        if (firebaseAuthentication.getId() == null) {
            this.entityManager.persist(firebaseAuthentication);
        } else {
            this.entityManager.merge(firebaseAuthentication);
        }
    }
}
