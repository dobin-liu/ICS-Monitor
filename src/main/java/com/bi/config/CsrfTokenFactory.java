package com.bi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;

/**
 * 處理 CSRF 的安全機制
 * 
 * @author Mingfu
 * @version
 *
 */
@Component
public class CsrfTokenFactory {

    @Value("${session.cookie.path:/}")
    private String sessionCookiePath;

    private CookieCsrfTokenRepository tokenRepository = null;

    @Bean
    public void init() {
        tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        tokenRepository.setCookiePath(sessionCookiePath);
        tokenRepository.setSecure(false);
    }

    public CsrfTokenRepository getCsrfTokenRepository() {
        return tokenRepository;
    }
}
