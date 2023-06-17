package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.data.request.BoardInfoCreate;
import com.illdangag.iricom.server.data.request.BoardInfoSearch;
import com.illdangag.iricom.server.data.request.BoardInfoUpdate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.BoardInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
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

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public BoardInfo createBoardInfo(@Valid BoardInfoCreate boardInfoCreate) {
        Board board = Board.builder()
                .title(boardInfoCreate.getTitle())
                .description(boardInfoCreate.getDescription())
                .build();
        this.boardRepository.save(board);
        return new BoardInfo(board);
    }

    @Override
    public BoardInfo getBoardInfo(String id) {
        Board board = this.getBoard(id);
        return new BoardInfo(board);
    }

    @Override
    public BoardInfoList getBoardInfoList(@Valid BoardInfoSearch boardInfoSearch) {
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

    public BoardInfo updateBoardInfo(String id, BoardInfoUpdate boardInfoUpdate) {
        try {
            long boardId = Long.parseLong(id);
            Board board = this.boardRepository.getBoard(boardId).orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
            return this.updateBoardInfo(board, boardInfoUpdate);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }
    }

    @Override
    public BoardInfo updateBoardInfo(Board board, @Valid BoardInfoUpdate boardInfoUpdate) {
        if (boardInfoUpdate.getTitle() != null) {
            board.setTitle(boardInfoUpdate.getTitle());
        }
        if (boardInfoUpdate.getDescription() != null) {
            board.setDescription(boardInfoUpdate.getDescription());
        }
        if (boardInfoUpdate.getEnabled() != null) {
            board.setEnabled(boardInfoUpdate.getEnabled());
        }

        this.boardRepository.save(board);
        return new BoardInfo(board);
    }

    private Board getBoard(String id) {
        Optional<Board> boardOptional = this.boardRepository.getBoard(id);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }
}
