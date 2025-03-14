/*
 * @(#)SessionConcurrencyFilter.java Nov 14, 2019
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 此 Filter 物件主要是針對登入帳號檢核，是否同時有不同登入 session 使用同一個帳號
 * 
 * @author Mingfu
 * @version
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SessionConcurrencyFilter extends OncePerRequestFilter {

    private final static String LOGGEDOUT = "您帳號已在其他載具使用中，已被登出！";

    @Autowired
    protected ObjectMapper jsonObjectMapper;

    // @Autowired
    // private AdmUserSessionsManager admUserSessionsManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // final HttpSession session = request.getSession();
        //
        // log.debug(MecUtil.toLogStr(request.getRequestURI()));
        //
        // if (session == null) {
        // filterChain.doFilter(request, response);
        // return;
        // }
        //
        // final String sessionId = request.getRequestedSessionId();
        // final MecLogin login = (MecLogin) session.getAttribute(LoginSession._NAME);
        //
        // if (login != null) {
        // final String userCode = login.getUserCode();
        // log.debug("login porfile pass - " + userCode + ":{} ", DevUtils.isPassAccount(userCode));
        // if (!DevUtils.isPassAccount(userCode)) {
        // final int count = admUserSessionsManager.concurrencyRefresh(userCode, sessionId);
        // if (count > 0) {
        // log.debug("userCode :{} is login other device, and currency will logout!", userCode);
        //
        // final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //
        // if (authentication != null) {
        // log.debug("auth:{}", authentication.getPrincipal());
        // }
        // new SecurityContextLogoutHandler().logout(request, response, null);
        //
        // response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // final RestErrorInfo data = new RestErrorInfo(LOGGEDOUT, "449", Collections.EMPTY_LIST);
        // response.getOutputStream().write(jsonObjectMapper.writeValueAsString(data).getBytes("UTF-8"));
        // response.flushBuffer();
        // return;
        // }
        // }
        //
        // }

        filterChain.doFilter(request, response);

    }

}
