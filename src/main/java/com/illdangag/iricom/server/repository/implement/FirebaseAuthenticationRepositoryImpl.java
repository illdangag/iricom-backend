package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.FirebaseAuthentication;
import com.illdangag.iricom.server.repository.FirebaseAuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class FirebaseAuthenticationRepositoryImpl implements FirebaseAuthenticationRepository {
    private final EntityManager entityManager;

    @Autowired
    public FirebaseAuthenticationRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Optional<FirebaseAuthentication> getFirebaseAuthentication(String id) {
        this.entityManager.clear();
        final String jpql = "SELECT fa FROM FirebaseAuthentication fa WHERE fa.id = :id";
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
        EntityTransaction transaction = this.entityManager.getTransaction();
        transaction.begin();
        if (firebaseAuthentication.getId() == null) {
            this.entityManager.persist(firebaseAuthentication);
        } else {
            this.entityManager.merge(firebaseAuthentication);
        }
        transaction.commit();
    }
}
