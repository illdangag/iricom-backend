package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.data.request.BoardInfoCreate;
import com.illdangag.iricom.server.data.request.BoardInfoSearch;
import com.illdangag.iricom.server.data.request.BoardInfoUpdate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.BoardInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.repository.CommentRepository;
import com.illdangag.iricom.server.repository.PostRepository;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class BoardServiceImpl extends IricomService implements BoardService {
    private final BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public BoardServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                            PostRepository postRepository, CommentRepository commentRepository,
                            BoardAuthorizationService boardAuthorizationService) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
        this.boardAuthorizationService = boardAuthorizationService;
    }

    @Override
    public BoardInfo createBoardInfo(String accountId, @Valid BoardInfoCreate boardInfoCreate) {
        Account account = this.getAccount(accountId);
        return this.createBoardInfo(account, boardInfoCreate);
    }

    /**
     * 게시판 생성
     */
    @Override
    public BoardInfo createBoardInfo(Account account, @Valid BoardInfoCreate boardInfoCreate) {
        if (account.getAuth() != AccountAuth.SYSTEM_ADMIN) { // 시스템 관리자가 아닌 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_CREATE_BOARD);
        }

        Board board = Board.builder()
                .title(boardInfoCreate.getTitle())
                .description(boardInfoCreate.getDescription())
                .enabled(boardInfoCreate.getEnabled())
                .undisclosed(boardInfoCreate.getUndisclosed())
                .notificationOnly(boardInfoCreate.getNotificationOnly())
                .build();

        this.boardRepository.save(board);

        boolean isAdmin = this.boardAuthorizationService.hasAuthorization(account, board);
        return new BoardInfo(board, isAdmin);
    }

    /**
     * 게시판 정보 조회
     */
    @Override
    public BoardInfo getBoardInfo(String boardId) {
        Board board = this.getBoard(boardId);
        this.validate(null, board);
        return new BoardInfo(board, false);
    }

    @Override
    public BoardInfo getBoardInfo(String accountId, String boardId) {
        Account account = this.getAccount(accountId);
        return this.getBoardInfo(account, boardId);
    }

    /**
     * 게시판 정보 조회
     */
    @Override
    public BoardInfo getBoardInfo(Account account, String boardId) {
        Board board = this.getBoard(boardId);
        this.validate(account, board);

        boolean isAdmin = this.boardAuthorizationService.hasAuthorization(account, board);
        return new BoardInfo(board, isAdmin);
    }

    /**
     * 공개 게시판 목록 조회
     */
    @Override
    public BoardInfoList getBoardInfoList(@Valid BoardInfoSearch boardInfoSearch) {
        List<Board> boardList;
        long totalBoardCount;

        boardList = this.boardRepository.getBoardList(null, boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled(), boardInfoSearch.getSkip(), boardInfoSearch.getLimit());
        totalBoardCount = this.boardRepository.getBoardCount(null, boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled());

        List<BoardInfo> boardInfoList = boardList.stream().map((board) -> {
            return new BoardInfo(board, false);
        }).collect(Collectors.toList());

        return BoardInfoList.builder()
                .total(totalBoardCount)
                .skip(boardInfoSearch.getSkip())
                .limit(boardInfoSearch.getLimit())
                .boardInfoList(boardInfoList)
                .build();
    }

    @Override
    public BoardInfoList getBoardInfoList(String accountId, @Valid BoardInfoSearch boardInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getBoardInfoList(account, boardInfoSearch);
    }

    /**
     * 공개, 비공개 게시판 목록 조회
     */
    @Override
    public BoardInfoList getBoardInfoList(Account account, @Valid BoardInfoSearch boardInfoSearch) {
        List<Board> boardList;
        long totalBoardCount;

        boardList = this.boardRepository.getBoardList(account, boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled(), boardInfoSearch.getSkip(), boardInfoSearch.getLimit());
        totalBoardCount = this.boardRepository.getBoardCount(account, boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled());

        List<BoardInfo> boardInfoList = boardList.stream().map((board) -> {
            boolean isAdmin = this.boardAuthorizationService.hasAuthorization(account, board);
            return new BoardInfo(board, isAdmin);
        }).collect(Collectors.toList());

        return BoardInfoList.builder()
                .total(totalBoardCount)
                .skip(boardInfoSearch.getSkip())
                .limit(boardInfoSearch.getLimit())
                .boardInfoList(boardInfoList)
                .build();
    }

    @Override
    public BoardInfo updateBoardInfo(String accountId, String boardId, @Valid BoardInfoUpdate boardInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updateBoardInfo(account, boardId, boardInfoUpdate);
    }

    /**
     * 게시판 정보 수정
     */
    @Override
    public BoardInfo updateBoardInfo(Account account, String boardId, @Valid BoardInfoUpdate boardInfoUpdate) {
        Board board = this.getBoard(boardId);
        return this.updateBoardInfo(account, board, boardInfoUpdate);
    }

    /**
     * 게시판 정보 수정
     */
    @Override
    public BoardInfo updateBoardInfo(Account account, Board board, @Valid BoardInfoUpdate boardInfoUpdate) {
        if (account.getAuth() != AccountAuth.SYSTEM_ADMIN) { // 시스템 관리자가 아닌 경우
            throw new IricomException(IricomErrorCode.INVALID_AUTHORIZATION_TO_CREATE_BOARD);
        }

        if (boardInfoUpdate.getTitle() != null) {
            board.setTitle(boardInfoUpdate.getTitle());
        }

        if (boardInfoUpdate.getDescription() != null) {
            board.setDescription(boardInfoUpdate.getDescription());
        }

        if (boardInfoUpdate.getEnabled() != null) {
            board.setEnabled(boardInfoUpdate.getEnabled());
        }

        if (boardInfoUpdate.getNotificationOnly() != null) {
            board.setNotificationOnly(boardInfoUpdate.getNotificationOnly());
        }

        if (boardInfoUpdate.getUndisclosed() != null) {
            board.setUndisclosed(boardInfoUpdate.getUndisclosed());
        }

        this.boardRepository.save(board);

        boolean isAdmin = this.boardAuthorizationService.hasAuthorization(account, board);
        return new BoardInfo(board, isAdmin);
    }
}
