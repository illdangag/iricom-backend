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

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) { // @Auth 어노테이션이 설정되지 않은 메서드를 호출 한 경우
            // 인증 및 인가를 확인하지 않음
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        AuthRole role = auth.role();
        if (role == AuthRole.NONE) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        FirebaseToken firebaseToken = this.getFirebaseToken(request);
        FirebaseAuthentication firebaseAuthentication = this.getFirebaseAuthentication(firebaseToken);
        Account account = firebaseAuthentication.getAccount();
        this.accountRepository.saveAccount(account);

        if (role == AuthRole.SYSTEM_ADMIN) {
            // 시스템 관리자
            this.checkAccount(account);
            this.checkAccountDetail(account);
            this.checkSystemAdmin(account);
        } else if (role == AuthRole.BOARD_ADMIN) {
            // 게시판 관리자
            this.checkAccount(account);
            this.checkAccountDetail(account);
            List<Board> boardList = this.checkBoardAdmin(account);
            request.setAttribute("boards", boardList.toArray(new Board[0]));
        } else if (role == AuthRole.ACCOUNT) {
            // 등록된 계정
            this.checkAccount(account);
            this.checkAccountDetail(account);
        } else if (role == AuthRole.UNREGISTERED_ACCOUNT) {
            // 등롣괴지 않은 계정
            this.checkAccount(account);
        }

        request.setAttribute("account", account);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void checkAccount(Account account) {
        if (account == null) {
            throw new IricomException(IricomErrorCode.NOT_REGISTERED_ACCOUNT);
        }
    }

    private void checkAccountDetail(Account account) {
        Optional<AccountDetail> accountDetailOptional = this.accountRepository.getAccountDetail(account);
        if (accountDetailOptional.isEmpty()) {
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

    private FirebaseToken getFirebaseToken(HttpServletRequest request) throws IricomException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.debug("Invalid header. Authorization: {}", authorization);
            throw new IricomException(IricomErrorCode.NOT_EXIST_FIREBASE_ID_TOKEN);
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

        return firebaseToken;
    }

    private FirebaseAuthentication getFirebaseAuthentication(FirebaseToken firebaseToken) {
        Optional<FirebaseAuthentication> firebaseAuthenticationOptional = this.firebaseAuthenticationRepository.getFirebaseAuthentication(firebaseToken.getUid());
        FirebaseAuthentication firebaseAuthentication;
        if (firebaseAuthenticationOptional.isEmpty()) {
            // 로그인 후 첫 API 호출인 경우, firebase 정보에 대하여 iricom 게정 생성
            Account account = null;
            List<Account> accountList = this.accountRepository.getAccountList(firebaseToken.getEmail());
            if (accountList.isEmpty()) {
                account = Account.builder()
                        .email(firebaseToken.getEmail())
                        .build();
            } else {
                account = accountList.get(0);
            }
            firebaseAuthentication = new FirebaseAuthentication(firebaseToken.getUid(), account);

            this.accountRepository.saveAccount(account);
            this.firebaseAuthenticationRepository.save(firebaseAuthentication);
        } else {
            firebaseAuthentication = firebaseAuthenticationOptional.get();
        }

        return firebaseAuthentication;
    }
}
