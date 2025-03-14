/*
 * @(#)RestAuthenticationProvider.java Oct 15, 2019
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestAuthenticationProvider implements AuthenticationProvider {

    // @Autowired
    // private MecUserDetailsManager userDetailsManager;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // final String loginId = authentication.getName();
        // String ape = authentication.getCredentials().toString();
        // // should Authenticate Against ThirdParty System
        // UserDetails userDetails;
        // try {
        // final String hostIp = InetAddress.getLocalHost().getHostAddress();
        // final CustomWebAuthenticationDetails wad = (CustomWebAuthenticationDetails) authentication.getDetails();
        //
        // final String clientIp = wad.getRemoteAddress();
        // final HttpServletRequest request = wad.getRequest();
        //
        // userDetails = userDetailsManager.verify(loginId, ape, hostIp, clientIp, request);
        // }
        // catch (final RestAuthenticationException e) {
        // throw e;
        // }
        // catch (final Exception e) {
        // log.error(MecUtil.SafeErrorLog(e));
        // throw new RestAuthenticationException(AuthStatus.DEFAULT_ERROR);
        // }
        // ape = null;

        final Authentication r = null;
        return r;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
