package com.bi.config;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

import com.bi.base.config.AppStaticConfig;

//--For Spring Boot Application Model
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class BIApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    // -- Spring Boot org.apache.logging.log4j
    // final Logger log = LoggerFactory.getLogger(BIApplicationContextInitializer.class); //-- org.slf4j.Logger
    final Logger log = LogManager.getLogger(BIApplicationContextInitializer.class); // -- org.apache.logging.log4j.Logger

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final ConfigurableEnvironment environment = applicationContext.getEnvironment();

        final String[] initialProfiles = environment.getActiveProfiles();
        if (log.isDebugEnabled()) {
            log.debug("Spring initial profiles: " + Arrays.toString(initialProfiles));
        }
        final String profile = AppStaticConfig.BI_ACTIVE_PROFILE;
        log.debug("BI_ACTIVE_PROFILE is {}", profile);

    }

}