package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;
import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.request.BoardInfoByBoardAdminSearch;
import com.illdangag.iricom.server.data.response.*;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.repository.BoardAdminRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@Service
public class BoardAuthorizationServiceImpl implements BoardAuthorizationService {
    private final BoardRepository boardRepository;
    private final BoardAdminRepository boardAdminRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Autowired
    public BoardAuthorizationServiceImpl(BoardAdminRepository boardAdminRepository, BoardRepository boardRepository,
                                         AccountRepository accountRepository, AccountService accountService) {
        this.boardAdminRepository = boardAdminRepository;
        this.boardRepository = boardRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    /**
     * 게시판 관리자 권한 추가
     */
    @Override
    public BoardAdminInfo createBoardAdminAuth(@Valid BoardAdminInfoCreate boardAdminInfoCreate) {
        Account account = this.getAccount(boardAdminInfoCreate.getAccountId());
        if (account.getAccountDetail() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_DETAIL_TO_UPDATE_BOARD_ADMIN);
        }

        Board board = this.getBoard(boardAdminInfoCreate.getBoardId());

        // 해당 게시판에 관리자로 이미 추가된 경우에는 추가로 등록하지 않도록 함
        Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getBoardAdmin(board, account);
        if (boardAdminOptional.isEmpty()) {
            BoardAdmin boardAdmin = BoardAdmin.builder()
                    .account(account)
                    .board(board)
                    .build();
            this.boardAdminRepository.save(boardAdmin);
        }

        if (account.getAuth() == AccountAuth.ACCOUNT) { // 일반 계정인 경우
            // 게시판 관리자 계정으로 정보 수정
            account.setAuth(AccountAuth.BOARD_ADMIN);
            this.accountService.saveAccount(account);
        }

        return this.getBoardAdminInfo(board);
    }

    /**
     * 게시판 관리지 권한 삭제
     */
    @Override
    public BoardAdminInfo deleteBoardAdminAuth(@Valid BoardAdminInfoDelete boardAdminInfoDelete) {
        Account account = this.getAccount(boardAdminInfoDelete.getAccountId());
        Board board = this.getBoard(boardAdminInfoDelete.getBoardId());

        // 이미 동일 계정과 게시판으로 권한이 있는 경우 권한을 삭제
        Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getBoardAdmin(board, account);
        if (boardAdminOptional.isPresent()) {
            BoardAdmin boardAdmin = boardAdminOptional.get();
            this.boardAdminRepository.delete(boardAdmin);
        }

        List<BoardAdmin> boardAdminList = this.boardAdminRepository.getBoardAdminList(account, null, null);
        if (boardAdminList.isEmpty()) { // 관리자 권한이 남아 있지 않은 경우
            account.setAuth(AccountAuth.ACCOUNT);
            this.accountService.saveAccount(account);
        } else if (account.getAuth() == AccountAuth.ACCOUNT) { // 관리자 권한이 남아 있는데 계정 권한이 일반 계정인 경우
            account.setAuth(AccountAuth.BOARD_ADMIN);
            this.accountService.saveAccount(account);
        }

        return this.getBoardAdminInfo(board);
    }

    /**
     * 게시판 관리자 목록 조회
     */
    @Override
    public BoardAdminInfoList getBoardAdminInfoList(Account account, @Valid BoardAdminInfoSearch boardAdminInfoSearch) {
        // 게시판 조회
        List<Board> boardList;
        long totalBoardCount;
        boardList = this.boardRepository.getBoardList(account, boardAdminInfoSearch.getKeyword(), boardAdminInfoSearch.getEnabled(), boardAdminInfoSearch.getSkip(), boardAdminInfoSearch.getLimit());
        totalBoardCount = this.boardRepository.getBoardCount(account, boardAdminInfoSearch.getKeyword(), boardAdminInfoSearch.getEnabled());

        List<BoardAdmin> boardAdminList = this.boardAdminRepository.getBoardAdminList(boardList);

        List<BoardAdminInfo> boardAdminInfoList = new LinkedList<>();
        for (Board board : boardList) {
            List<AccountInfo> accountInfoList = boardAdminList.stream()
                    .filter(boardAdmin -> boardAdmin.getBoard().equals(board))
                    .map(boardAdmin -> {
                        Account boardAdminAccount = boardAdmin.getAccount();
                        return this.accountService.getAccountInfo(boardAdminAccount);
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
    public BoardAdminInfo getBoardAdminInfo(String boardId) {
        Board board = this.getBoard(boardId);
        return this.getBoardAdminInfo(board);
    }

    @Override
    public BoardInfoList getBoardInfoListByBoardAdmin(Account account, @Valid BoardInfoByBoardAdminSearch boardInfoByBoardAdminSearch) {
        int skip = boardInfoByBoardAdminSearch.getSkip();
        int limit = boardInfoByBoardAdminSearch.getLimit();

        List<BoardInfo> boardInfoList = null;
        long total = 0;

        List<Board> boardList = this.boardRepository.getBoardList(account, null, null, skip, limit);
        total = this.boardRepository.getBoardCount(account, null, null);
        boardInfoList = boardList.stream()
                .map((board) -> {
                    return new BoardInfo(board, null);
                })
                .collect(Collectors.toList());

        return BoardInfoList.builder()
                .boardInfoList(boardInfoList)
                .total(total)
                .skip(skip)
                .limit(limit)
                .build();
    }

    private BoardAdminInfo getBoardAdminInfo(Board board) {
        List<BoardAdmin> boardAdminList = this.boardAdminRepository.getBoardAdminList(Collections.singletonList(board));

        BoardAdminInfo boardAdminInfo = new BoardAdminInfo(board);
        if (!boardAdminList.isEmpty()) {
            Set<BoardAdmin> boardAdminSet = boardAdminList.stream()
                    .sorted((item1, item2) -> {
                        return item1.getCreateDate().compareTo(item2.getCreateDate()) * -1;
                    }).collect(Collectors.toSet());
            List<AccountInfo> accountInfoList = boardAdminSet.stream()
                    .map(BoardAdmin::getAccount)
                    .map(this.accountService::getAccountInfo)
                    .collect(Collectors.toList());
            boardAdminInfo.setAccountInfoList(accountInfoList);
        } else {
            boardAdminInfo.setAccountInfoList(Collections.emptyList());
        }

        return boardAdminInfo;
    }

    @Override
    public boolean hasAuthorization(Account account, Board board) {
        if (account.getAuth() == AccountAuth.SYSTEM_ADMIN) {
            return true;
        }

        Optional<BoardAdmin> boardAdminOptional = this.boardAdminRepository.getBoardAdmin(board, account);
        if (boardAdminOptional.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Board getBoard(String id) {
        long boardId;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }
        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }

    private Account getAccount(String id) {
        Optional<Account> accountOptional = this.accountRepository.getAccount(id);
        return accountOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT));
    }
}
