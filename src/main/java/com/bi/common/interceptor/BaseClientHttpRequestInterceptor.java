/*
 * @(#)BaseClientHttpRequestInterceptor.java 2019年7月10日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.interceptor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.bi.common.util.MecUtil;
import com.bi.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author April
 * @version
 *
 */
@Slf4j
public class BaseClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    // private static final Logger log =LoggerFactory.getLogger(BaseClientHttpRequestInterceptor.class);
    private static final Logger intervalLogger = LoggerFactory.getLogger("intervalLog");
    private long startTime;
    private long endTime;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        final ClientHttpResponse response = execution.execute(request, body);
        traceResponse(request, response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        startTime = System.currentTimeMillis();
        log.info("===========================request begin================================================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        // log.debug("Headers     : {}", MecUtil.filterVaildLog(MecUtil.toJSONString(request.getHeaders())));
        log.debug("Request body: {}", new String(body, "UTF-8"));
        log.info("==========================request end================================================");
    }

    private void traceResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
        final StringBuilder inputStringBuilder = new StringBuilder();
        /*
         * final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8")); String line =
         * bufferedReader.readLine(); while (line != null) { inputStringBuilder.append(line); inputStringBuilder.append('\n'); line =
         * bufferedReader.readLine(); }
         */
        endTime = System.currentTimeMillis();
        final long elapsedTime = endTime - startTime;
        log.info("============================response begin==========================================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        log.debug("Status code  : {}", response.getStatusCode());
        log.debug("Status text  : {}", response.getStatusText());
        // log.debug("Headers      : {}", response.getHeaders());
        log.debug("Response body: {}", inputStringBuilder.toString());
        log.debug("startTime: {}", startTime);
        log.debug("endTime: {}", endTime);
        log.debug("elapsedTime: {}", elapsedTime);
        log.info("=======================response end=================================================");

        intervalLogger.info("{}\t,{}\t,{}\t,{}", DateUtil.format(DateUtil.getTimestamp(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS_DASH), "MEC",
                request.getURI(), elapsedTime);
    }
}
