package com.illdangag.iricom.server.configuration.interceptor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.ElapseLoggable;
import com.illdangag.iricom.server.configuration.config.FirebaseInitializer;
import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.repository.BoardAdminRepository;
import com.illdangag.iricom.server.repository.FirebaseAuthenticationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * firebase를 이용하여 요청에 대한 권한 검사
 */
@Slf4j
@Component
public class FirebaseAuthInterceptor implements HandlerInterceptor {
    private final FirebaseInitializer firebaseInitializer;
    private final AccountRepository accountRepository;
    private final FirebaseAuthenticationRepository firebaseAuthenticationRepository;
    private final BoardAdminRepository boardAdminRepository;

    @Autowired
    public FirebaseAuthInterceptor(FirebaseInitializer firebaseInitializer,
                                   FirebaseAuthenticationRepository firebaseAuthenticationRepository,
                                   AccountRepository accountRepository,
                                   BoardAdminRepository boardAdminRepository) {
        this.firebaseInitializer = firebaseInitializer;
        this.firebaseAuthenticationRepository = firebaseAuthenticationRepository;
        this.accountRepository = accountRepository;
        this.boardAdminRepository = boardAdminRepository;
    }

    @ElapseLoggable
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        // firebase 토큰이 존재 하면 해당 토큰에 대한 계정 정보를 조회
        Optional<FirebaseToken> firebaseTokenOptional = this.getFirebaseToken(request);
        Account account = null;
        if (firebaseTokenOptional.isPresent()) {
            // firebase 토큰으로 사용자를 조회
            FirebaseToken firebaseToken = firebaseTokenOptional.get();
            FirebaseAuthentication firebaseAuthentication = this.getFirebaseAuthentication(firebaseToken);
            account = firebaseAuthentication.getAccount();
            request.setAttribute("account", account); // 계정 정보를 api endpoint로 전달
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

        if (auth == null) { // @Auth 어노테이션이 설정되지 않은 메서드를 호출 한 경우
            // 인증 및 인가를 확인하지 않음
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } else { // @Auth 어노테이션이 설정된 경우 계정의 권한 정보를 확인
            AuthRole[] requireAuths = auth.role(); // api endpoint에서 요구하는 권한 정보
            List<AuthRole> requireAuthList = requireAuths != null ? Arrays.asList(requireAuths) : Collections.emptyList();

            if (!requireAuthList.isEmpty() && !requireAuthList.contains(AuthRole.NONE) && firebaseTokenOptional.isEmpty()) { // 권한이 필요하지만 firebase 토큰이 존재하지 않는 경우
                throw new IricomException(IricomErrorCode.NOT_EXIST_FIREBASE_ID_TOKEN); // 요청의 권한을 확인 할 수 없으므로 오류 처리
            } else {
                if (requireAuthList.contains(AuthRole.NONE) || requireAuthList.contains(AuthRole.UNREGISTERED_ACCOUNT)) { // 요구하는 권한이 등록되지 않은 사용자
                    // 별도로 확인하지 않음
                } else if (requireAuthList.contains(AuthRole.ACCOUNT)) { // 요구하는 권한이 사용자
                    this.checkAccount(account);
                    this.checkAccountDetail(account);
                } else if (requireAuthList.contains(AuthRole.BOARD_ADMIN)) { // 요구하는 권한이 게시판 관리자
                    this.checkAccount(account);
                    this.checkAccountDetail(account);
                    List<Board> boardList = null;
                    if (account.getAuth() == AccountAuth.SYSTEM_ADMIN) {
                        // 시스템 관리자는 관리자로 등록된 게시판이 없어도 허용
                        boardList = Collections.emptyList();
                    } else {
                        // 시스템 관리자가 아닌 경우에는 1개 이상의 게시판 관리자 권한이 있어야 함
                        boardList = this.checkBoardAdmin(account);
                    }
                    request.setAttribute("boards", boardList.toArray(new Board[0]));
                } else if (requireAuthList.contains(AuthRole.SYSTEM_ADMIN)) { // 요구하는 권한이 시스템 관리자
                    this.checkAccount(account);
                    this.checkSystemAdmin(account);
                }
            }
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
    }

    private void checkAccount(Account account) {
        if (account == null) {
            throw new IricomException(IricomErrorCode.NOT_REGISTERED_ACCOUNT);
        }
    }

    private void checkAccountDetail(Account account) {
        Optional<AccountDetail> accountDetailOptional = this.accountRepository.getAccountDetail(account);
        AccountDetail accountDetail = accountDetailOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_REGISTERED_ACCOUNT_DETAIL));
        String nickname = accountDetail.getNickname();
        if (nickname == null || nickname.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_REGISTERED_ACCOUNT_DETAIL);
        }
    }

    private List<Board> checkBoardAdmin(Account account) {
        List<BoardAdmin> boardAdminList = this.boardAdminRepository.getBoardAdminList(account, false);
        Set<BoardAdmin> set = new LinkedHashSet<>(boardAdminList);
        List<Board> boardList = set.stream()
                .map(BoardAdmin::getBoard)
                .sorted(Comparator.comparing(Board::getId))
                .collect(Collectors.toList());

        if (boardList.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_REGISTERED_BOARD_ADMIN);
        }
        return boardList;
    }

    private void checkSystemAdmin(Account account) {
        if (account.getAuth() != AccountAuth.SYSTEM_ADMIN) {
            throw new IricomException(IricomErrorCode.NOT_REGISTERED_SYSTEM_ADMIN);
        }
    }

    private Optional<FirebaseToken> getFirebaseToken(HttpServletRequest request) throws IricomException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authorization.substring(7);
        FirebaseToken firebaseToken;
        try {
            FirebaseAuth firebaseAuth = this.firebaseInitializer.getFirebaseAuth();
            firebaseToken = firebaseAuth.verifyIdToken(token);
        } catch (FirebaseAuthException exception) {
            if (exception.getAuthErrorCode().name().equals("EXPIRED_ID_TOKEN")) {
                // 토큰 만료
                throw new IricomException(IricomErrorCode.EXPIRED_FIREBASE_ID_TOKEN);
            } else {
                throw new IricomException(IricomErrorCode.INVALID_FIREBASE_ID_TOKEN);
            }
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_FIREBASE_ID_TOKEN);
        }

        return Optional.of(firebaseToken);
    }

    private FirebaseAuthentication getFirebaseAuthentication(FirebaseToken firebaseToken) {
        Optional<FirebaseAuthentication> firebaseAuthenticationOptional = this.firebaseAuthenticationRepository.getFirebaseAuthentication(firebaseToken.getUid());
        FirebaseAuthentication firebaseAuthentication;
        if (firebaseAuthenticationOptional.isEmpty()) {
            // 로그인 후 첫 API 호출인 경우, firebase 정보에 대하여 iricom 계정 생성
            Account account = null;
            List<Account> accountList = this.accountRepository.getAccountList(firebaseToken.getEmail());
            if (accountList.isEmpty()) {
                account = Account.builder()
                        .email(firebaseToken.getEmail())
                        .build();
            } else {
                account = accountList.get(0);
            }

            firebaseAuthentication = FirebaseAuthentication.builder()
                    .id(firebaseToken.getUid())
                    .account(account)
                    .build();
            this.accountRepository.saveAccount(account);

            AccountDetail accountDetail = account.getAccountDetail();
            if (accountDetail == null) {
                accountDetail = AccountDetail.builder()
                        .account(account)
                        .nickname("")
                        .description("")
                        .build();
                this.accountRepository.saveAccountDetail(accountDetail);
                account.setAccountDetail(accountDetail);
                this.accountRepository.saveAccount(account);
            }
            this.firebaseAuthenticationRepository.save(firebaseAuthentication);
        } else {
            firebaseAuthentication = firebaseAuthenticationOptional.get();
        }

        return firebaseAuthentication;
    }
}
