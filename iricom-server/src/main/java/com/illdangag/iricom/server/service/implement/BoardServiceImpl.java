package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.BoardInfoCreate;
import com.illdangag.iricom.server.data.request.BoardInfoSearch;
import com.illdangag.iricom.server.data.request.BoardInfoUpdate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.BoardInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    private final BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository, BoardAuthorizationService boardAuthorizationService) {
        this.boardRepository = boardRepository;
        this.boardAuthorizationService = boardAuthorizationService;
    }

    @Override
    public BoardInfo createBoardInfo(Account account, @Valid BoardInfoCreate boardInfoCreate) {
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

    @Override
    public BoardInfo getBoardInfo(String id) {
        Board board = this.getBoard(id);
        this.validate(null, board);
        return new BoardInfo(board, false);
    }

    @Override
    public BoardInfo getBoardInfo(Account account, String id) {
        Board board = this.getBoard(id);
        this.validate(account, board);

        boolean isAdmin = this.boardAuthorizationService.hasAuthorization(account, board);
        return new BoardInfo(board, isAdmin);
    }

    @Override
    public BoardInfoList getBoardInfoList(BoardInfoSearch boardInfoSearch) {
        List<Board> boardList;
        long totalBoardCount;

        if (boardInfoSearch.getEnabled() != null) {
            boardList = this.boardRepository.getBoardList(boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled(), boardInfoSearch.getSkip(), boardInfoSearch.getLimit());
            totalBoardCount = this.boardRepository.getBoardCount(boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled());
        } else {
            boardList = this.boardRepository.getBoardList(boardInfoSearch.getKeyword(), boardInfoSearch.getSkip(), boardInfoSearch.getLimit());
            totalBoardCount = this.boardRepository.getBoardCount(boardInfoSearch.getKeyword());
        }

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
    public BoardInfoList getBoardInfoList(Account account, BoardInfoSearch boardInfoSearch) {
        List<Board> boardList;
        long totalBoardCount;

        if (boardInfoSearch.getEnabled() != null) {
            boardList = this.boardRepository.getBoardList(account, boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled(), boardInfoSearch.getSkip(), boardInfoSearch.getLimit());
            totalBoardCount = this.boardRepository.getBoardCount(account, boardInfoSearch.getKeyword(), boardInfoSearch.getEnabled());
        } else {
            boardList = this.boardRepository.getBoardList(account, boardInfoSearch.getKeyword(), boardInfoSearch.getSkip(), boardInfoSearch.getLimit());
            totalBoardCount = this.boardRepository.getBoardCount(account, boardInfoSearch.getKeyword());
        }

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
    public BoardInfo updateBoardInfo(Account account, String id, BoardInfoUpdate boardInfoUpdate) {
        Board board = this.getBoard(id);
        return this.updateBoardInfo(account, board, boardInfoUpdate);
    }

    @Override
    public BoardInfo updateBoardInfo(Account account, Board board, BoardInfoUpdate boardInfoUpdate) {
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

    private Board getBoard(String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }

        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }

    private void validate(Account account, Board board) {
        if (board.getUndisclosed()) {
            if (account == null) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
            }

            List<Long> accessibleBoardIdList = this.boardRepository.getAccessibleBoardIdList(account);
            if (!accessibleBoardIdList.contains(board.getId())) {
                throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
            }
        }
    }
}
