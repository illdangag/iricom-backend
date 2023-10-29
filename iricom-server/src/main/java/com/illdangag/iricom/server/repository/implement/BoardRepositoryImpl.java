package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class BoardRepositoryImpl implements BoardRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public BoardRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Board> getBoard(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id = :id";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        List<Board> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public Optional<Board> getDisclosedBoard(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id = :id" +
                " AND b.undisclosed = false";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        List<Board> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public Optional<Board> getDisclosedBoard(Account account, long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> accessibleBoardIdList = getAccessibleBoardIdList(entityManager, account);
        final String jpql = "SELECT b FROM Board b LEFT JOIN BoardInAccountGroup biag ON b.id = biag.board.id" +
                " WHERE b.id = :id" +
                " AND (b.undisclosed = false OR b.id IN :boardIdList)";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
                .setParameter("id", id)
                .setParameter("boardIdList", accessibleBoardIdList);
        List<Board> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    private List<Long> getAccessibleBoardIdList(EntityManager entityManager, Account account) {
        List<Long> accountGroupBoardIdList = this.getAccountGroupBoardIdList(entityManager, account);
        List<Long> boardAdminBoardIdList = this.getBoardAdminBoardIdList(entityManager, account);

        Set<Long> set = new HashSet<>();
        set.addAll(accountGroupBoardIdList);
        set.addAll(boardAdminBoardIdList);
        return new ArrayList<>(set);
    }

    private List<Long> getAccountGroupBoardIdList(EntityManager entityManager, Account account) {
        List<Long> accountGroupIdList = null;

        if (account.getAuth() == AccountAuth.SYSTEM_ADMIN) {
            accountGroupIdList = this.getAccountGroupAll(entityManager);
        } else {
            accountGroupIdList = this.getAccountGroupId(entityManager, account);
        }

        final String jpql = "SELECT biag.board.id FROM BoardInAccountGroup biag" +
                " WHERE biag.accountGroup.id IN :accountGroupId";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("accountGroupId", accountGroupIdList);
        return query.getResultList();
    }

    private List<Long> getBoardAdminBoardIdList(EntityManager entityManager, Account account) {
        List<LocalDateTime> createDateList = this.getLastBoardAdminCreateDateList(entityManager, account);

        final String jpql = "SELECT ba.board.id" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " AND ba.createDate IN :createDateList" +
                " AND ba.deleted = false";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account)
                .setParameter("createDateList", createDateList);
        return query.getResultList();
    }

    private List<LocalDateTime> getLastBoardAdminCreateDateList(EntityManager entityManager, Account account) {
        final String maxJpql = "SELECT new Map(ba.account AS account, ba.board AS board, MAX(ba.createDate) AS createDate)" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " GROUP BY ba.account, ba.board";
        Query query = entityManager.createQuery(maxJpql)
                .setParameter("account", account);
        List<Map<String, Object>> maxCreateDateResultList = query.getResultList();
        return maxCreateDateResultList.stream().map(item -> (LocalDateTime) item.get("createDate"))
                .collect(Collectors.toList());
    }

    private List<Long> getAccountGroupId(EntityManager entityManager, Account account) {
        final String jpql = "SELECT ag.id FROM AccountGroup ag RIGHT JOIN AccountInAccountGroup aiag ON ag.id = aiag.accountGroup.id" +
                " WHERE ag.deleted = false AND aiag.account = :account";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getResultList();
    }

    private List<Long> getAccountGroupAll(EntityManager entityManager) {
        final String jpql = "SELECT ag.id FROM AccountGroup ag" +
                " WHERE ag.deleted = false";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getResultList();
    }

    @Override
    public List<Board> getBoardList(String title, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)" +
                " AND b.undisclosed = false" +
                " ORDER BY title";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(String title) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)" +
                " AND b.undisclosed = false";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(String title, boolean enabled, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)" +
                " AND b.enabled = :enabled" +
                " AND b.undisclosed = false";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(String title, boolean enabled) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)" +
                " AND b.enabled = :enabled" +
                " AND b.undisclosed = false";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(Account account, String title, boolean enabled, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> accessibleBoardIdList =  getAccessibleBoardIdList(entityManager, account);

        final String jpql = "SELECT b FROM Board b" +
                " WHERE (b.undisclosed = false OR b.id IN :boardIdList)" +
                " AND b.enabled = :enabled" +
                " AND UPPER(b.title) LIKE UPPER(:title)";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
                .setParameter("boardIdList", accessibleBoardIdList)
                .setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(Account account, String title, boolean enabled) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> accessibleBoardIdList =  getAccessibleBoardIdList(entityManager, account);

        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE (b.undisclosed = false OR b.id IN :boardIdList)" +
                " AND b.enabled = :enabled" +
                " AND UPPER(b.title) LIKE UPPER(:title)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("boardIdList", accessibleBoardIdList)
                .setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled);
        Long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(Account account, String title, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> accessibleBoardIdList =  getAccessibleBoardIdList(entityManager, account);

        final String jpql = "SELECT b FROM Board b" +
                " WHERE (b.undisclosed = false OR b.id IN :boardIdList)" +
                " AND UPPER(b.title) LIKE UPPER(:title)";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
                .setParameter("boardIdList", accessibleBoardIdList)
                .setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(Account account, String title) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> accessibleBoardIdList =  getAccessibleBoardIdList(entityManager, account);

        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE (b.undisclosed = false OR b.id IN :boardIdList)" +
                " AND UPPER(b.title) LIKE UPPER(:title)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("boardIdList", accessibleBoardIdList)
                .setParameter("title", "%" + StringUtils.escape(title) + "%");
        Long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Long> getAccessibleBoardIdList(Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        List<Long> boardIdList = this.getAccessibleBoardIdList(entityManager, account);
        entityManager.close();
        return boardIdList;
    }

    @Override
    public boolean existBoard(List<Long> boardIdList) {
        Set<Long> boardIdSet = new HashSet<>(boardIdList);
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE b.id IN (:boardIdSet)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("boardIdSet", boardIdSet);

        long result = query.getSingleResult();

        if (boardIdSet.size() == result) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void save(Board board) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (board.getId() == null) {
            entityManager.persist(board);
        } else {
            entityManager.merge(board);
        }

        entityTransaction.commit();
        entityManager.close();
    }
}
