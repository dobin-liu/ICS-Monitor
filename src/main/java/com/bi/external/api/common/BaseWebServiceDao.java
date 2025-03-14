/*
 * @(#)AbstractTliWebServiceDao.java 2018年8月23日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.external.api.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import com.bi.common.model.RestErrorCode;
import com.bi.spring.rest.client.DefaultWebserviceClient;
import com.bi.spring.rest.exception.BIRestException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseWebServiceDao {

    protected static final String SLASH = "/";
    private String classRequestMapping = ""; // classRequestMapping

    @Autowired
    protected ObjectMapper jsonObjectMapper;

    /**
     * @param classRequestMapping annotation of API class
     */
    protected BaseWebServiceDao(String classRequestMapping) {
        this.classRequestMapping = classRequestMapping; // initilized ClassRequestMapping
    }

    protected abstract DefaultWebserviceClient getApi();

    protected BaseWebServiceDao() {
        classRequestMapping = ""; // initilized ClassRequestMapping
    }

    /**
     * do Create UriTemplate
     *
     * @param methodRequestMapping
     * @return UriTemplate
     */
    protected final UriTemplate getUriTemplate(String methodRequestMapping) {
        return getApi().createTemplate(getPath(methodRequestMapping));
    }

    /**
     * @param methodRequestMapping
     * @return String requestMapping + methodRequestMapping
     *
     */
    protected final String getPath(String methodRequestMapping) {
        final String uriString = UriComponentsBuilder.newInstance().path(getClassRequestMapping()).pathSegment(methodRequestMapping).toUriString();
        return uriString;
    }

    protected final String getClassRequestMapping() {
        return classRequestMapping;
    }

    protected BIRestException genServerException(Exception e, String path) {
        return new BIRestException(HttpStatus.OK, RestErrorCode.ERROR514, "Call '" + getApi().getRootUrl() + getPath(path) + "' Error", e);
    }

}
