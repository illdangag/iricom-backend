package com.illdangag.iricom.core.service.implement;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.entity.BoardAdmin;
import com.illdangag.iricom.core.data.entity.type.AccountAuth;
import com.illdangag.iricom.core.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.core.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.core.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.core.data.request.BoardInfoByBoardAdminSearch;
import com.illdangag.iricom.core.data.response.*;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.repository.AccountRepository;
import com.illdangag.iricom.core.repository.BoardRepository;
import com.illdangag.iricom.core.repository.CommentRepository;
import com.illdangag.iricom.core.repository.PostRepository;
import com.illdangag.iricom.core.service.AccountService;
import com.illdangag.iricom.core.service.BoardAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@Transactional
@Service
public class BoardAuthorizationServiceImpl extends IricomService implements BoardAuthorizationService {
    private final AccountService accountService;

    @Autowired
    public BoardAuthorizationServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                                         PostRepository postRepository, CommentRepository commentRepository,
                                         AccountService accountService) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
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
        List<BoardAdmin> boardAdminList = this.boardRepository.getBoardAdminList(board, account);
        if (boardAdminList.isEmpty()) {
            BoardAdmin boardAdmin = BoardAdmin.builder()
                    .account(account)
                    .board(board)
                    .build();
            this.boardRepository.save(boardAdmin);
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
        List<BoardAdmin> boardAdminList = this.boardRepository.getBoardAdminList(board, account);
        for (BoardAdmin boardAdmin : boardAdminList) {
            this.boardRepository.delete(boardAdmin);
        }

        List<BoardAdmin> otherBoardAdminList = this.boardRepository.getBoardAdminList(account);
        if (account.getAuth() != AccountAuth.SYSTEM_ADMIN && otherBoardAdminList.isEmpty()) { // 관리자 권한이 남아 있지 않은 경우
            account.setAuth(AccountAuth.ACCOUNT);
            this.accountService.saveAccount(account);
        } else if (account.getAuth() == AccountAuth.ACCOUNT) { // 관리자 권한이 남아 있는데 계정 권한이 일반 계정인 경우
            account.setAuth(AccountAuth.BOARD_ADMIN);
            this.accountService.saveAccount(account);
        }

        return this.getBoardAdminInfo(board);
    }

    @Override
    public BoardAdminInfoList getBoardAdminInfoList(String accountId, @Valid BoardAdminInfoSearch boardAdminInfoSearch) {
        Account account = this.getAccount(accountId);
        return this.getBoardAdminInfoList(account, boardAdminInfoSearch);
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

        List<BoardAdmin> boardAdminList = this.boardRepository.getBoardAdminList(boardList);

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

        List<Board> boardList = this.boardRepository.getBoardListInBoardAdmin(account, skip, limit);
        total = this.boardRepository.getBoardCountInBoardAdmin(account);
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
        List<BoardAdmin> boardAdminList = this.boardRepository.getBoardAdminList(Collections.singletonList(board));

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

        List<BoardAdmin> boardAdminList = this.boardRepository.getBoardAdminList(board, account);
        return !boardAdminList.isEmpty();
    }
}
