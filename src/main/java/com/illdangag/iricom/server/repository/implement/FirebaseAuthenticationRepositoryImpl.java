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
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public FirebaseAuthenticationRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<FirebaseAuthentication> getFirebaseAuthentication(String id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT fa FROM FirebaseAuthentication fa WHERE fa.id = :id";
        TypedQuery<FirebaseAuthentication> query = entityManager.createQuery(jpql, FirebaseAuthentication.class);
        query.setParameter("id", id);
        List<FirebaseAuthentication> firebaseAuthenticationList = query.getResultList();
        entityManager.close();

        if (firebaseAuthenticationList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(firebaseAuthenticationList.get(0));
        }
    }

    @Override
    public void save(FirebaseAuthentication firebaseAuthentication) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (firebaseAuthentication.getId() == null) {
            entityManager.persist(firebaseAuthentication);
        } else {
            entityManager.merge(firebaseAuthentication);
        }
        transaction.commit();
        entityManager.close();
    }
}
