package com.bi.defauleWeb;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.bi.config.BIApplicationContextInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        SpringApplicationBuilder sab = application.sources(Application.class);
        application.initializers(new BIApplicationContextInitializer());
        return sab;
    }

}
