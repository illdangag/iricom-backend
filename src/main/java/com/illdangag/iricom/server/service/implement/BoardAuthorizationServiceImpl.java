package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountAuth;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;
import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.BoardAdminRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@Service
public class BoardAuthorizationServiceImpl implements BoardAuthorizationService {
    private final AccountService accountService;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final BoardAdminRepository boardAdminRepository;

    @Autowired
    public BoardAuthorizationServiceImpl(AccountService accountService, BoardService boardService, BoardAdminRepository boardAdminRepository,
                                         BoardRepository boardRepository) {
        this.accountService = accountService;
        this.boardService = boardService;
        this.boardAdminRepository = boardAdminRepository;
        this.boardRepository = boardRepository;
    }

    /**
     * ????????? ????????? ?????? ??????
     */
    public void createBoardAdminAuth(BoardAdminInfoCreate boardAdminInfoCreate) {
        Account account = this.accountService.getAccount(boardAdminInfoCreate.getAccountId());
        if (account.getAccountDetail() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_DETAIL_TO_UPDATE_BOARD_ADMIN);
        }

        Board board = this.boardService.getBoard(boardAdminInfoCreate.getBoardId());

        Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getBoardAdmin(board, account);
        if (boardAdminOptional.isEmpty() || boardAdminOptional.get().getDeleted()) {
            // ????????? ?????? ???????????? ????????? ????????? ?????? ????????? ?????? ???????????? ????????? ?????? ????????????
            BoardAdmin boardAdmin = BoardAdmin.builder()
                    .account(account)
                    .board(board)
                    .deleted(false)
                    .build();
            this.boardAdminRepository.save(boardAdmin);
        }

        if (account.getAuth() == AccountAuth.ACCOUNT) {
            // ?????? ????????? ????????? ????????? ????????? ???????????? ?????? ??????
            account.setAuth(AccountAuth.BOARD_ADMIN);
            this.accountService.saveAccount(account);
        }
    }

    /**
     * ????????? ????????? ?????? ??????
     */
    @Override
    public void deleteBoardAdminAuth(BoardAdminInfoDelete boardAdminInfoDelete) {
        Account account = this.accountService.getAccount(boardAdminInfoDelete.getAccountId());
        Board board = this.boardService.getBoard(boardAdminInfoDelete.getBoardId());

        // ?????? ?????? ????????? ??????????????? ????????? ?????? ?????? ????????? ??????
        Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getBoardAdmin(board, account);
        if (boardAdminOptional.isPresent() && !boardAdminOptional.get().getDeleted()) {
            // ????????? ?????? ???????????? ????????? ?????? ????????????
            BoardAdmin boardAdmin = BoardAdmin.builder()
                    .account(account)
                    .board(board)
                    .deleted(true)
                    .build();
            this.boardAdminRepository.save(boardAdmin);
        }

        // ????????? ????????? ????????? ?????? ?????? ?????? ??????
        // ?????? ????????? ?????? ???????????? ??????
        List<BoardAdmin> boardAdminList = this.boardAdminRepository.getBoardAdminList(account, false);
        if (boardAdminList.isEmpty()) {
            account.setAuth(AccountAuth.ACCOUNT);
            this.accountService.saveAccount(account);
        }
    }

    /**
     * ????????? ????????? ?????? ??????
     */
    @Override
    public BoardAdminInfoList getBoardAdminInfoList(@Valid BoardAdminInfoSearch boardAdminInfoSearch) {
        // ????????? ??????
        List<Board> boardList;
        long totalBoardCount;

        if (boardAdminInfoSearch.getEnabled() != null) {
            boardList = this.boardRepository.getBoardList(boardAdminInfoSearch.getKeyword(), boardAdminInfoSearch.getEnabled(), boardAdminInfoSearch.getSkip(), boardAdminInfoSearch.getLimit());
            totalBoardCount = this.boardRepository.getBoardCount(boardAdminInfoSearch.getKeyword(), boardAdminInfoSearch.getEnabled());
        } else {
            boardList = this.boardRepository.getBoardList(boardAdminInfoSearch.getKeyword(), boardAdminInfoSearch.getSkip(), boardAdminInfoSearch.getLimit());
            totalBoardCount = this.boardRepository.getBoardCount(boardAdminInfoSearch.getKeyword());
        }

        List<BoardAdmin> boardAdminList = this.boardAdminRepository.getBoardAdminList(boardList);

        Set<BoardAdmin> filteredBoardAdminSet = new LinkedHashSet<>(boardAdminList);
        List<BoardAdmin> filteredBoardList = filteredBoardAdminSet.stream()
                .filter(boardAdmin -> !boardAdmin.getDeleted())
                .collect(Collectors.toList()); // ????????? ????????? ????????? ??????

        List<BoardAdminInfo> boardAdminInfoList = new LinkedList<>();
        for (Board board : boardList) {
            List<AccountInfo> accountInfoList = filteredBoardList.stream()
                    .filter(boardAdmin -> boardAdmin.getBoard().equals(board))
                    .map(boardAdmin -> {
                        Account account = boardAdmin.getAccount();
                        return this.accountService.getAccountInfo(account);
                    }).sorted(Comparator.comparing(AccountInfo::getEmail)).collect(Collectors.toList());
            BoardAdminInfo boardAdminInfo = new BoardAdminInfo(board);
            boardAdminInfo.setAccountInfoList(accountInfoList);
            boardAdminInfoList.add(boardAdminInfo);
        }

        return BoardAdminInfoList.builder()
                .total(totalBoardCount)
                .skip(boardAdminInfoSearch.getSkip())
                .limit(boardAdminInfoSearch.getLimit())
                .boardAdminInfoList(boardAdminInfoList)
                .build();
    }

    @Override
    public BoardAdmin getBoardAdmin(Account account, Board board) {
        Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getEnableBoardAdmin(board, account);
        return boardAdminOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD_ADMIN));
    }
}
