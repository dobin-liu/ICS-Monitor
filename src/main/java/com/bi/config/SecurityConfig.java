/*
 * @(#)SecurityConfig.java 2019年7月3日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.config;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.bi.base.config.AppStaticConfig;
import com.bi.filter.SessionConcurrencyFilter;
import com.bi.security.RestAuthenticationEntryPoint;
import com.bi.security.RestAuthenticationFailureHandler;
import com.bi.security.RestAuthenticationProvider;
import com.bi.security.RestAuthenticationSuccessHandler;
import com.bi.security.RestLogoutHandler;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@ComponentScan("com.bi.mec.security")
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] PERMIT_PATTERNS = {//
            "/api/**", //
            "/public/**", //
            "/v2/api-docs", //
            "/v3/api-docs", //
            "/swagger-resources/**", //
            "/swagger-ui/**", // for springfox-boot-starter 3.x
            "/swagger-ui.html", //
            "/webjars/**", //
            "/captcha"//
    };
    private static final String[] PROD_PERMIT_PATTERNS = {// 正式環境關閉 swagger 功能
            "/api/**", //
            "/public/**", //
            "/webjars/**", //
            "/captcha", //
            "/v2/api-docs", //
            "/v3/api-docs", //
            "/swagger-resources/**", //
            "/swagger-ui/**", // for springfox-boot-starter 3.x
            "/swagger-ui.html", //
    };

    // @Autowired
    // private RestCorsFilter corsFilter;

    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Autowired
    private CsrfTokenFactory csrfTokenFactory;

    @Autowired
    private SessionConcurrencyFilter sessionConcurrencyFilter;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private RestAuthenticationProvider restAuthenticationProvider;

    @Autowired
    private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;

    @Autowired
    private RestAuthenticationFailureHandler restAuthenticationFailureHandler;

    @Autowired
    private RestLogoutHandler restLogoutHandler;

    // @Autowired
    // private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] permitPatterns = null;
        if (AppStaticConfig.isProfileProd()) {// 正式環境關閉 swagger 功能
            permitPatterns = PROD_PERMIT_PATTERNS;
        }
        else {
            permitPatterns = PERMIT_PATTERNS;
        }
     // @formatter:off
        http.cors().and() // 跨域支持
            // .csrf().disable()
            .authorizeRequests().antMatchers(permitPatterns).permitAll().anyRequest().authenticated()
            .and()
            .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .formLogin()
            //??.authenticationDetailsSource(authenticationDetailsSource) //--加上自定驗證物件
            .successHandler(restAuthenticationSuccessHandler)
            .failureHandler(restAuthenticationFailureHandler)
            .and()
            .logout()
            .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
            .addLogoutHandler(restLogoutHandler)
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true);
            

        http.addFilterBefore(sessionConcurrencyFilter, SessionManagementFilter.class);
        http.headers().httpStrictTransportSecurity().maxAgeInSeconds(31536000); 
        http.headers().frameOptions().sameOrigin();
        http.headers().xssProtection().and().contentSecurityPolicy("script-src 'self'");
        
        //csrf設定
        http.csrf().csrfTokenRepository(csrfTokenFactory.getCsrfTokenRepository());
        http.csrf().requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher());
        
        // @formatter:on        
    }

    public class CsrfSecurityRequestMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest request) {
            String[] permitPatterns = null;
            String[] permitPatterns2 = {"/login"};
            if (AppStaticConfig.isProfileProd()) {
                permitPatterns = PROD_PERMIT_PATTERNS;
            }
            else {
                permitPatterns = PERMIT_PATTERNS;
            }
            for (int i = 0; i < permitPatterns.length; i++) {
                AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(permitPatterns[i]);
                if (antPathRequestMatcher.matches(request)) {
                    return false;
                }
            }
            for (int i = 0; i < permitPatterns2.length; i++) {
                AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(permitPatterns2[i]);
                if (antPathRequestMatcher.matches(request)) {
                    return false;
                }
            }
            return true; // 回true前端要配合修改，送 X-XSRF-TOKEN
            // return false;
        }
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {

        auth.authenticationProvider(restAuthenticationProvider).eraseCredentials(true);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();
    }

    CorsConfigurationSource corsConfigurationSource() {

        final CorsConfiguration configuration = new CorsConfiguration();
        // log.debug("allowedOrigins : {}", allowedOrigins.toString());
        configuration.setAllowedOrigins(allowedOrigins);// 若設AllowCredentials true，此AllowedOrigins : * 則無法產生效果
        // configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // 是否允許攜帶cookies
        configuration.setExposedHeaders(Arrays.asList("X-Auth-Token"));
        configuration.setMaxAge(Long.valueOf(1800));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "X-Auth-Token", "Authorization"));
        configuration.setAllowedMethods(Arrays.asList(//
                HttpMethod.GET.name(), //
                HttpMethod.POST.name(), //
                HttpMethod.PATCH.name(), //
                HttpMethod.PUT.name(), //
                HttpMethod.DELETE.name()//
        ));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
