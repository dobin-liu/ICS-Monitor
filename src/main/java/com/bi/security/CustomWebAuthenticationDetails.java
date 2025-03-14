package com.bi.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.bi.common.util.MecUtil;

import javax.servlet.http.HttpServletRequest;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6094221115452045151L;
    private HttpServletRequest httpServletRequest;
    private String remoteAddress = "";

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        remoteAddress = MecUtil.getRemoteAddr(request);
        httpServletRequest = request;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public String toString() {
        // --避免 元壽 WAS 環境中未知狀況的例外
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("RemoteIpAddress: ").append(this.remoteAddress).append("; ");
        sb.append("SessionId: ").append(this.getSessionId());
        return sb.toString();
    }

    public HttpServletRequest getRequest() {
        return httpServletRequest;
    }
}
