package com.illdangag.iricom.server.confgiuration.interceptor;

import com.illdangag.iricom.server.configuration.config.FirebaseInitializer;
import com.illdangag.iricom.server.configuration.interceptor.FirebaseAuthInterceptor;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.repository.FirebaseAuthenticationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@Primary
@Component
public class MockAuthInterceptor extends FirebaseAuthInterceptor implements HandlerInterceptor {
    private Map<String, Long> accountTokenIdMap = new HashMap<>();
    private final AccountRepository accountRepository;

    @Autowired
    public MockAuthInterceptor(FirebaseInitializer firebaseInitializer, FirebaseAuthenticationRepository firebaseAuthenticationRepository, AccountRepository accountRepository, BoardRepository boardRepository) {
        super(firebaseInitializer, firebaseAuthenticationRepository, accountRepository, boardRepository);
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    protected Optional<Account> getAccount(HttpServletRequest request) {
        String token = this.getAuthToken(request);
        if (this.accountTokenIdMap.containsKey(token)) {
            Long accountId = accountTokenIdMap.get(token);
            return this.accountRepository.getAccount(accountId);
        } else {
            return Optional.empty();
        }
    }

    public String setAccount(Account account) {
        String token = UUID.randomUUID().toString();
        this.accountTokenIdMap.put(token, account.getId());
        return token;
    }
}
