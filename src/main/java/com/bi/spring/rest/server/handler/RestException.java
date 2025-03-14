/*
 * @(#)RestException.java 2018年5月24日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.spring.rest.server.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.bi.spring.rest.exception.BIRestException;

/**
 * 
 * @author Mingfu
 * @version 1.0
 *
 */
public class RestException extends BIRestException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3396321279871231610L;
    private final String logMessage;
    private List<String> alertMessages = new ArrayList<String>();

    public RestException(HttpStatus status, String statusCode, String message, String logMessage, List<String> alertMessages) {
        super(status, statusCode, message);
        this.logMessage = logMessage;
        this.alertMessages = alertMessages;
    }

    public RestException(HttpStatus status, String statusCode, String message, Throwable causes, String logMessage, List<String> alertMessages) {
        super(status, statusCode, message, causes);
        this.logMessage = logMessage;
        this.alertMessages = alertMessages;
    }

    public static RestException createBusinessLogicException(String statusCode, String message, String logMessage, List<String> alertMessages) {
        return new RestException(DEFAULT_BL_ERROR_STATUS, statusCode, message, logMessage, alertMessages);
    }

    public static RestException createBLServerException(String statusCode, String message, Throwable causes, String logMessage,
            List<String> alertMessages) {
        return new RestException(DEFAULT_BL_ERROR_STATUS, statusCode, message, causes, logMessage, alertMessages);
    }

    public static RestException createServerException(String statusCode, String message, String logMessage, List<String> alertMessages) {
        return new RestException(HttpStatus.OK, statusCode, message, logMessage, alertMessages);
    }

    public String getLogMessage() {
        return logMessage;
    }

    public List<String> getAlertMessages() {
        return alertMessages;
    }
}
