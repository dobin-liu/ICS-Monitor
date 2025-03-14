/*
 * @(#)SwaggerConfig.java Aug 27, 2019
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved. 
 */

package com.bi.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
// @EnableSwagger2WebMvc // for for 2.10.x
// @EnableSwagger2 // for 2.10.x 以前 / 3.x.x 以後
@Profile({"dev", "lsit", "sit", "uat", "prod"})
public class SwaggerConfig {

    private static final String VERSION = "1.0.0";
    private static final String TITLE = "ICS-Utility-API";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final List<ResponseMessage> GLOBAL_RESPONSES = Arrays.asList(//
            new ResponseMessageBuilder().code(200).message("OK").build(), //
            new ResponseMessageBuilder().code(400).message("Bad Request").build(), //
            new ResponseMessageBuilder().code(401).message("Unauthorized").build(), //
            new ResponseMessageBuilder().code(404).message("Page not found").build(), //
            new ResponseMessageBuilder().code(460).message("Param error").build(), //
            new ResponseMessageBuilder().code(461).message("Param Missing").build(), //
            new ResponseMessageBuilder().code(500).message("Internal Error").build(), //
            new ResponseMessageBuilder().code(512).message("SQL Exception").build(), //
            new ResponseMessageBuilder().code(514).message("Call external api fail").build()//
    );

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)//
                .globalResponseMessage(RequestMethod.GET, GLOBAL_RESPONSES)//
                .globalResponseMessage(RequestMethod.POST, GLOBAL_RESPONSES)//
                .globalResponseMessage(RequestMethod.DELETE, GLOBAL_RESPONSES)//
                .globalResponseMessage(RequestMethod.PATCH, GLOBAL_RESPONSES)//
                // .securitySchemes(Lists.newArrayList(apiKey()))//
                .apiInfo(buildApiInf()).select()//
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))//
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo buildApiInf() {
        return new ApiInfoBuilder()//
                .title(TITLE)//
                .contact(new springfox.documentation.service.Contact("BI", "", ""))//
                .version(VERSION)//
                .build();
    }
}
