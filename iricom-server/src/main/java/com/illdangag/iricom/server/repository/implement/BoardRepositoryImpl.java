package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Transactional
@Repository
public class BoardRepositoryImpl implements BoardRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 게시판 조회
     */
    @Override
    public Optional<Board> getBoard(Long id) {
        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id = :id";

        TypedQuery<Board> query = this.entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        List<Board> resultList = query.getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    /**
     * 게시판 목록 조회
     */
    @Override
    public List<Board> getBoardList(List<Long> idList) {
        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id IN :idList";

        TypedQuery<Board> query = this.entityManager.createQuery(jpql, Board.class)
                .setParameter("idList", idList);
        List<Board> resultList = query.getResultList();
        return resultList;
    }

    /**
     * 게시판 목록 조회
     */
    @Override
    public List<Board> getBoardList(Account account, String title, Boolean enabled, Integer offset, Integer limit) {
        String jpql = "SELECT b FROM Board b";

        List<Long> accessibleBoardIdList = null;
        if (account != null) {
            accessibleBoardIdList = getAccessibleBoardIdList(account);
            jpql += " WHERE (b.undisclosed = false OR b.id IN :boardIdList)";
        } else {
            jpql += " WHERE b.undisclosed = false";
        }

        if (enabled != null) {
            jpql += " AND b.enabled = :enabled";
        }

        if (title != null) {
            jpql += " AND UPPER(b.title) LIKE UPPER(:title)";
        }

        TypedQuery<Board> query = this.entityManager.createQuery(jpql, Board.class);
        if (account != null) {
            query.setParameter("boardIdList", accessibleBoardIdList);
        }

        if (enabled != null) {
            query.setParameter("enabled", enabled);
        }

        if (title != null) {
            query.setParameter("title", "%" + StringUtils.escape(title) + "%");
        }

        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        List<Board> resultList = query.getResultList();
        return resultList;
    }

    /**
     * 게시판 목록 수 조회
     */
    @Override
    public long getBoardCount(Account account, String title, Boolean enabled) {
        String jpql = "SELECT COUNT(*) FROM Board b";

        List<Long> accessibleBoardIdList = null;
        if (account != null) {
            accessibleBoardIdList = getAccessibleBoardIdList(account);
            jpql += " WHERE (b.undisclosed = false OR b.id IN :boardIdList)";
        } else {
            jpql += " WHERE b.undisclosed = false";
        }

        if (enabled != null) {
            jpql += " AND b.enabled = :enabled";
        }

        if (title != null) {
            jpql += " AND UPPER(b.title) LIKE UPPER(:title)";
        }

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        if (account != null) {
            query.setParameter("boardIdList", accessibleBoardIdList);
        }

        if (enabled != null) {
            query.setParameter("enabled", enabled);
        }

        if (title != null) {
            query.setParameter("title", "%" + StringUtils.escape(title) + "%");
        }

        Long result = query.getSingleResult();
        return result;
    }

    @Override
    public void save(Board board) {
        if (board.getId() == null) {
            entityManager.persist(board);
        } else {
            entityManager.merge(board);
        }
    }

    /**
     * 게시판 목록에 대해서 게시판 관리자 조회
     */
    @Override
    public List<BoardAdmin> getBoardAdminList(List<Board> boardList) {
        final String jpql = "SELECT ba FROM BoardAdmin ba" +
                " WHERE ba.board IN :boards" +
                " ORDER BY ba.board.title ASC, ba.account.email ASC, ba.createDate DESC";

        TypedQuery<BoardAdmin> query = this.entityManager.createQuery(jpql, BoardAdmin.class)
                .setParameter("boards", boardList);

        List<BoardAdmin> resultList = query.getResultList();
        return resultList;
    }

    /**
     * 계정이 게시판 관리자로 등록된 게시판 관리자 조회
     */
    @Override
    public List<BoardAdmin> getBoardAdminList(Account account) {
        String jpql = "SELECT ba" +
                " FROM BoardAdmin ba";

        if (account != null) {
            jpql += " WHERE ba.account = :account";
        }

        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class);

        if (account != null) {
            query.setParameter("account", account);
        }

        List<BoardAdmin> boardAdminList = query.getResultList();
        return boardAdminList;
    }

    /**
     * 게시판에 계정이 게시판 관리자 여부 조회
     */
    @Override
    public List<BoardAdmin> getBoardAdminList(Board board, Account account) {
        final String jpql = "SELECT ba FROM BoardAdmin ba" +
                " WHERE ba.board = :board" +
                " AND ba.account = :account" +
                " ORDER BY ba.createDate DESC";

        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class)
                .setParameter("board", board)
                .setParameter("account", account);

        List<BoardAdmin> resultList = query.getResultList();
        return resultList;
    }

    private List<Long> getAccessibleBoardIdList(Account account) {
        List<Long> accountGroupBoardIdList = this.getAccountGroupBoardIdList(account);
        List<Long> boardAdminBoardIdList = this.getBoardAdminBoardIdList(account);

        Set<Long> set = new HashSet<>();
        set.addAll(accountGroupBoardIdList);
        set.addAll(boardAdminBoardIdList);
        return new ArrayList<>(set);
    }

    private List<Long> getAccountGroupBoardIdList(Account account) {
        List<Long> accountGroupIdList = null;

        if (account.getAuth() == AccountAuth.SYSTEM_ADMIN) {
            accountGroupIdList = this.getAccountGroupAll();
        } else {
            accountGroupIdList = this.getAccountGroupId(account);
        }

        final String jpql = "SELECT biag.board.id FROM BoardInAccountGroup biag" +
                " WHERE biag.accountGroup.id IN :accountGroupId";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("accountGroupId", accountGroupIdList);
        return query.getResultList();
    }

    private List<Long> getBoardAdminBoardIdList(Account account) {
        final String jpql = "SELECT ba.board.id" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);

        return query.getResultList();
    }

    private List<Long> getAccountGroupId(Account account) {
        final String jpql = "SELECT ag.id FROM AccountGroup ag RIGHT JOIN AccountInAccountGroup aiag ON ag.id = aiag.accountGroup.id" +
                " WHERE aiag.account = :account";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getResultList();
    }

    private List<Long> getAccountGroupAll() {
        final String jpql = "SELECT ag.id FROM AccountGroup ag";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        return query.getResultList();
    }

    @Override
    public void save(BoardAdmin boardAdmin) {
        if (boardAdmin.getId() == null) {
            this.entityManager.persist(boardAdmin);
        } else {
            this.entityManager.merge(boardAdmin);
        }
    }

    @Override
    public void delete(BoardAdmin boardAdmin) {
        BoardAdmin entity = this.entityManager.find(BoardAdmin.class, boardAdmin.getId());
        this.entityManager.remove(entity);
    }
}
