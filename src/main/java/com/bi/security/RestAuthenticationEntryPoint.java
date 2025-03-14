/*
 * @(#)RestAuthenticationEntryPoint.java 2019年7月3日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mingfu
 * @version
 *
 */
@Component
@Slf4j
public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private final static String REALM_NAME = "ICS-Utility-API";

    @Override
    public void afterPropertiesSet() {
        setRealmName(REALM_NAME);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
        // log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
        log.error("Responding with unauthorized error");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

    }

}
