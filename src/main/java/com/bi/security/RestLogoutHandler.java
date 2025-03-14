/*
 * @(#)RestLogoutHandler.java Nov 11, 2019
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestLogoutHandler implements LogoutHandler {

    // @Autowired
    // private AdmUsersManager admUsersManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        // final MecUserDetails userDetails = (MecUserDetails) authentication.getPrincipal();
        // if (userDetails == null) {
        // return;
        // }
        // final MecLogin login = userDetails.getLogin();
        // final String userCode = login.getUserCode();
        // final String hostIp = request.getRemoteHost();
        // final String clientIp = MecUtil.getRemoteAddr(request);
        // final HttpSession session = request.getSession();
        // final String sessionId = session.getId();
        // // 刪除登入資料 && 儲存登出資料
        // admUsersManager.saveLogoutInfo(userCode, hostIp, clientIp, sessionId);
        log.debug("RestLogoutHandler success!");
    }

}
