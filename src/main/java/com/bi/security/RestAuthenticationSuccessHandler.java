/*
 * @(#)RestAuthenticationSuccessHandler.java 2019年7月3日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.bi.common.util.MecUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final static String WILL_LOGOUT = "您帳號已在其他載具使用中，前者將被登出！";
    private final static String METHOD_NAME_LOGIN = "登入";
    private final static String ACTION_MSG_LOGIN = "登入成功";
    private final static String PROG_CLASS_NAME_LOGIN = "TEST3";

    private RequestCache requestCache = new HttpSessionRequestCache();
    private static final String REQUEST_HEAD_USER_AGENT = "user-agent";

    @Autowired
    protected ObjectMapper jsonObjectMapper;

    // @Autowired
    // private AdmUserSessionsManager admUserSessionsManager;

    // @Autowired
    // private AdmUserAccessLogsManager admUserAccessLogsManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        HttpSession session = request.getSession(true);
        if (session != null && !session.isNew()) {
            logger.debug("session will be invalidate");
            session.invalidate();
        }
        session = request.getSession(true);
        final String sessionId = session.getId();
        final String remoteIp = MecUtil.getRemoteAddr(request);
        // --加上 Header 資訊
//        logger.debug(MecUtil.getAllHeadersLog(request));
        // final MecUserDetails userDetails = (MecUserDetails) authentication.getPrincipal();
        // final MecLogin login = userDetails.getLogin();
        // final MecLogin loginExt = new MecLoginImpl();
        // BeanUtils.copyProperties(login, loginExt);
        // final AdmUsers user = userDetails.getAdmUsers();
        // // 儲存登入資訊
        // final String userCode = user.getUser_code();
        // final int timeout_m = session.getMaxInactiveInterval() / 60;
        // logger.debug(String.valueOf(timeout_m));
        // final int concurrency = admUserSessionsManager.concurrencyLogin(userCode, sessionId, remoteIp, timeout_m);
        //
        // session.removeAttribute(LoginSession._NAME);
        //
        // // load remoteIp/browserType/browserVersion to login
        // // login.setRemoteIp(remoteIp);
        // if (StrUtil.isNotEmpty(remoteIp) && remoteIp.matches(MecUtil.PATTERN_IP4)) {
        // loginExt.setRemoteIp(String.valueOf(remoteIp));
        // }
        // // login.setBrowserParams(request.getHeader(REQUEST_HEAD_USER_AGENT));
        // final String userAgent = MecUtil.toFilterStr(request.getHeader(REQUEST_HEAD_USER_AGENT));
        // if (StrUtil.isNotEmpty(userAgent)) {
        // final String[] browserInfo = BrowserEnum.getBrowserInfo(userAgent);
        // loginExt.setBrowserType(browserInfo[0]);
        // loginExt.setBrowserVersion(browserInfo[1]);
        // }
        // // session.setAttribute(LoginSession._NAME, login);
        // Serializable sz = (Serializable) login;
        // session.setAttribute(LoginSession._NAME, sz);
        //
        // admUserAccessLogsManager.insertLog(PROG_CLASS_NAME_LOGIN, METHOD_NAME_LOGIN, null, null, ACTION_MSG_LOGIN, loginExt);
        //
        // final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        // if (concurrency > 0) {
        // response.setStatus(HttpStatus.OK.value());
        // params.add("msg", WILL_LOGOUT);
        //
        // }
        // params.add("userName", login.getUserName());
        // response.getOutputStream().write(jsonObjectMapper.writeValueAsString(params).getBytes("UTF-8"));
        // response.flushBuffer();
        //
        // if (savedRequest == null) {
        // clearAuthenticationAttributes(request);
        // return;
        // }
        // final String targetUrlParam = getTargetUrlParameter();
        // if (isAlwaysUseDefaultTargetUrl() || (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
        // requestCache.removeRequest(request, response);
        // clearAuthenticationAttributes(request);
        // return;
        // }

        clearAuthenticationAttributes(request);

    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

}
