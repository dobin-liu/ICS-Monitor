/*
 * @(#)WSException.java 2019年12月20日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.spring.rest.server.handler;

/**
 * 
 * @author Mingfu
 * @version 1.0
 *
 */
public class WSException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final Integer OK = 0;

    public static final Integer INFO = 100;

    public static final Integer NOTE = 200;

    public static final Integer CHECK = 300;

    public static final Integer ERROR = 500;

    private Integer code;

    private String url;

    private String info;

    private String alertMessgae;

    public WSException(String message) {
        super(message);
    }

    public WSException(String message, Throwable cause) {
        super(message, cause);
    }

    public WSException(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAlertMessgae() {
        return alertMessgae;
    }

    public void setAlertMessgae(String alertMessgae) {
        this.alertMessgae = alertMessgae;
    }
}
