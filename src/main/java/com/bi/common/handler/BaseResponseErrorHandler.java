/*
 * @(#)BaseResponseErrorHandler.java 2018年2月6日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.handler;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.bi.common.model.RestErrorCode;
import com.bi.spring.rest.exception.BIRestException;

import lombok.extern.slf4j.Slf4j;

/**
 * 取代原BiResponseErrorHandler 將所有錯誤轉為String
 *
 * @author April
 * @version 1.0
 *
 */
@Slf4j
public class BaseResponseErrorHandler extends DefaultResponseErrorHandler {

    // private static final Logger logger =LoggerFactory.getLogger(BaseResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        final HttpStatus status = response.getStatusCode();
        String message = null;

        try {
            final byte[] bytes = getResponseBody(response);
            if (ArrayUtils.isNotEmpty(bytes)) {
                message = new String(bytes, getCharset(response));
            }
        }
        catch (final BIRestException e) {
            // log.error("error parsing reponse body for status: " + status, e);
            log.error("error parsing reponse body for status");
        }

        if (StringUtils.isEmpty(message)) {
            message = response.getStatusText();
        }

        throw new BIRestException(status, RestErrorCode.ERROR514, message); // Call external api fail

    }
}
