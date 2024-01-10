package com.illdangag.iricom.storage.repository.impl;

import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@Repository
public class FileRepositoryImpl implements FileRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<FileMetadata> getFileMetadata(UUID id) {
        final String jpql = "SELECT fmd FROM FileMetadata fmd" +
                " WHERE fmd.id = :id" +
                " AND fmd.deleted = false";

        TypedQuery<FileMetadata> query = this.entityManager.createQuery(jpql, FileMetadata.class)
                .setParameter("id", id);

        List<FileMetadata> resultList = query.getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void saveFileMetadata(FileMetadata fileMetadata) {
        if (fileMetadata.getId() == null) {
            this.entityManager.persist(fileMetadata);
        } else {
            entityManager.merge(fileMetadata);
        }
        this.entityManager.flush();
    }
}
