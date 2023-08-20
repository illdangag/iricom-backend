package com.illdangag.iricom.storage.repository.impl;

import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class FileRepositoryImpl implements FileRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public FileRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<FileMetadata> getFileMetadata(UUID id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        final String jpql = "SELECT fmd FROM FileMetadata fmd" +
                " WHERE fmd.id = :id" +
                " AND fmd.deleted = false";

        TypedQuery<FileMetadata> query = entityManager.createQuery(jpql, FileMetadata.class)
                .setParameter("id", id);

        List<FileMetadata> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void saveFileMetadata(FileMetadata fileMetadata) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        if (fileMetadata.getId() == null) {
            entityManager.persist(fileMetadata);
        } else {
            entityManager.merge(fileMetadata);
        }

        transaction.commit();
        entityManager.close();
    }
}
