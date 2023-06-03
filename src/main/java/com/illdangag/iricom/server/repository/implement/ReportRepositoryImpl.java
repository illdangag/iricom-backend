package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostReport;
import com.illdangag.iricom.server.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReportRepositoryImpl implements ReportRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ReportRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<PostReport> getPostReport(Account account, Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pr from PostReport pr " +
                "WHERE pr.account = :account " +
                "AND pr.post = :post";

        TypedQuery<PostReport> query = entityManager.createQuery(jpql, PostReport.class)
                .setParameter("account", account)
                .setParameter("post", post);
        List<PostReport> reportList = query.getResultList();
        entityManager.close();

        return reportList;
    }

    @Override
    public void savePostReport(PostReport postReport) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (postReport.getId() == null) {
            entityManager.persist(postReport);
        } else {
            entityManager.merge(postReport);
        }
        transaction.commit();
        entityManager.close();
    }
}