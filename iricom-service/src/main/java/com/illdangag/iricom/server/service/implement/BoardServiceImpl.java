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
import com.illdangag.iricom.server.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public BoardInfo createBoardInfo(BoardInfoCreate boardInfoCreate) {
        Board board = Board.builder()
                .title(boardInfoCreate.getTitle())
                .description(boardInfoCreate.getDescription())
                .enabled(boardInfoCreate.getEnabled())
                .undisclosed(boardInfoCreate.getUndisclosed())
                .notificationOnly(boardInfoCreate.getNotificationOnly())
                .build();

        this.boardRepository.save(board);
        return new BoardInfo(board);
    }

    @Override
    public BoardInfo getBoardInfo(String id) {
        Board board = this.getDiscloseBoard(id);
        return new BoardInfo(board);
    }

    @Override
    public BoardInfo getBoardInfo(Account account, String id) {
        Board board = this.getDisclosedBoard(account, id);
        return new BoardInfo(board);
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

        List<BoardInfo> boardInfoList = boardList.stream().map(BoardInfo::new).collect(Collectors.toList());

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

        List<BoardInfo> boardInfoList = boardList.stream().map(BoardInfo::new).collect(Collectors.toList());

        return BoardInfoList.builder()
                .total(totalBoardCount)
                .skip(boardInfoSearch.getSkip())
                .limit(boardInfoSearch.getLimit())
                .boardInfoList(boardInfoList)
                .build();
    }

    @Override
    public BoardInfo updateBoardInfo(String id, BoardInfoUpdate boardInfoUpdate) {
        Board board = this.getBoard(id);
        return this.updateBoardInfo(board, boardInfoUpdate);
    }

    @Override
    public BoardInfo updateBoardInfo(Board board, BoardInfoUpdate boardInfoUpdate) {
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

        this.boardRepository.save(board);
        return new BoardInfo(board);
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

    private Board getDiscloseBoard(String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }
        Optional<Board> boardOptional = this.boardRepository.getDisclosedBoard(boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }

    private Board getDisclosedBoard(Account account, String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }

        Optional<Board> boardOptional = this.boardRepository.getDisclosedBoard(account, boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }
}
