/*
 * @(#)BaseHandlerInterceptor.java 2019年4月11日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bi.common.util.MecUtil;
import com.bi.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bLake
 * @version
 *
 */
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    // private final Logger log =LoggerFactory.getLogger(LoggerInterceptor.class);
    private final Logger logger2 = LoggerFactory.getLogger("intervalLog");

    private static final String START_TIME = LoggerInterceptor.class + "_startTime";
    private static final String REQUEST_ID = LoggerInterceptor.class + "_requestId";
    // private long startTime;
    // private long endTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String requestId = UUID.randomUUID().toString();
        final long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);
        request.setAttribute(REQUEST_ID, requestId);
        log.info("[start][id:{}][{}][{}][startTime : {} ms]", requestId, request.getMethod(), MecUtil.toLogStr(request.getRequestURI()), startTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView)
            throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        final long startTime = (Long) request.getAttribute(START_TIME);
        final String requestId = (String) request.getAttribute(REQUEST_ID);
        final long endTime = System.currentTimeMillis();
        final long elapsedTime = endTime - startTime;
        log.info("[end][id:{}][{}][{}][startTime : {} ms][endTime : {} ms][elapsedTime :{} ms]", requestId, request.getMethod(),
                MecUtil.toLogStr(request.getRequestURI()), startTime, endTime, elapsedTime);
        logger2.info("{}\t,{}\t,{}\t,{}", DateUtil.format(DateUtil.getTimestamp(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS_DASH), "bi",
                MecUtil.toLogStr(request.getRequestURI()), elapsedTime);
    }

}
