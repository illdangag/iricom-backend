package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostBan;
import com.illdangag.iricom.server.repository.BanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class BanRepositoryImpl implements BanRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public BanRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<PostBan> getPostBanList(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBan pb" +
                " WHERE pb.post = :post" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBan> query = entityManager.createQuery(jpql, PostBan.class)
                .setParameter("post", post);
        List<PostBan> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostBanCount(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostBan pb" +
                " WHERE pb.post = :post";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public void savePostBan(PostBan postBan) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (postBan.getId() == null) {
            entityManager.persist(postBan);
        } else {
            entityManager.merge(postBan);
        }
        transaction.commit();
        entityManager.close();
    }
}
