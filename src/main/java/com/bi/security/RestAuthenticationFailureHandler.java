/*
 * @(#)RestAuthenticationFailureHandler.java Oct 15, 2019
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mingfu
 * @version
 *
 */
@Component
@Slf4j
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
    // private final Logger logger =LoggerFactory.getLogger(RestAuthenticationFailureHandler.class);
    public static final String UNAUTHORIZED = "401";
    private final static String METHOD_NAME_LOGIN = "登入";
    private final static String ACTION_MSG_LOGIN_FAIL = "登入失敗";
    private final static String PROG_CLASS_NAME_LOGIN = "TEST3";
    @Autowired
    protected ObjectMapper jsonObjectMapper;

    // @Autowired
    // private AdmUserAccessLogsManager admUserAccessLogsManager;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // final RestAuthenticationException failureHandler = (RestAuthenticationException) exception;
        // RestErrorInfo data;
        // if (failureHandler.getErrorCode().equals(RestErrorCode.ERROR517)) { // WSException 元壽服務異常回傳 拋回前端顯示訊息
        // // TODO 弱掃低風險問題 之後調整
        // // 弱掃 Information Exposure Through an Error Message 避開修正
        // VaildFieldData<AuthenticationException> errObject = new VaildFieldData<AuthenticationException>("errObject");
        // errObject.addValue(exception);
        // // data = new RestErrorInfo(MecUtil.SafeErrorLog(exception), RestErrorCode.ERROR517, Arrays.asList(failureHandler.getMessage()));
        // data = new RestErrorInfo(errObject.getValue("errObject").getMessage(), RestErrorCode.ERROR517,
        // Arrays.asList(failureHandler.getMessage()));
        // }
        // else { // 登入異常 判斷對應代碼及訊息
        // // TODO 弱掃低風險問題 之後調整
        // // 弱掃 Information Exposure Through an Error Message 避開修正
        // // data = new RestErrorInfo(MecUtil.SafeErrorLog(exception), checkApiStatusCode(failureHandler.getErrorCode()),
        // // Collections.emptyList());
        // VaildFieldData<AuthenticationException> errObject02 = new VaildFieldData<AuthenticationException>("errObject02");
        // errObject02.addValue(exception);
        // data = new RestErrorInfo(errObject02.getValue("errObject02").getMessage(), checkApiStatusCode(failureHandler.getErrorCode()),
        // Collections.emptyList());
        // }
        // log.debug("RestErrorInfo :{}", jsonObjectMapper.writeValueAsString(data));
        // // --Fix For Missing HSTS Header
        // response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        // final String msg = MecUtil.genString(data.message);
        // RestErrorInfo data2 = new RestErrorInfo(data.message, "401", Collections.emptyList());
        // response.getOutputStream().write(jsonObjectMapper.writeValueAsString(data2).getBytes("UTF-8"));
        // response.flushBuffer();
        // saveUserAccessLog(request);

    }

}
