/*
 * @(#)MyWebAppInitializer.java 2019年7月3日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.twelvemonkeys.servlet.image.IIOProviderContextListener;

import lombok.extern.slf4j.Slf4j;

/**
 * For Web Container Like Tomcat / Jboss / WebSphere
 * 
 * @author Mingfu at Jan 6, 2020
 *
 */
@Configuration
@Slf4j
@ComponentScan(basePackages = {"com.bi"})
public class BiWebAppInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext servletContext) throws ServletException {
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        sessionCookieConfig.setName("MEC_JSESSIONID");
        sessionCookieConfig.setHttpOnly(true);
        // --必須得在 https 環境下，cookie 才會正常!
        // sessionCookieConfig.setSecure(true);
        // sessionCookieConfig.setPath("/");

        // --註冊 UTF-8 編碼，解決 Spring MVC 的 HandlerMapping 中文亂碼問題
        registerCharachterEncodingFilter(servletContext);
        log.debug(servletContext.getSessionCookieConfig().toString());
        servletContext.addListener(httpSessionListener(servletContext));

        // 設定時區
        System.setProperty("user.timezone", "Asia/Taipei");
        
        servletContext.addListener(IIOProviderContextListener.class);
        
        log.info("web init startup");
    }

    private void registerCharachterEncodingFilter(ServletContext aContext) {

        final CharacterEncodingFilter cef = new CharacterEncodingFilter();
        cef.setForceEncoding(true);
        cef.setEncoding("UTF-8");
        aContext.addFilter("charachterEncodingFilter", cef).addMappingForUrlPatterns(null, true, "/*");

    }

    @Bean // bean for http session listener
    public HttpSessionListener httpSessionListener(ServletContext servletContext) {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) { // This method will be called when session created
                // log.info("Session Created with session id:" + se.getSession().getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) { // This method will be automatically called when session destroyed
                // String sessionId = se.getSession().getId();
                // log.info("Session Destroyed, Session id:" + sessionId);
            }
        };
    }

}
