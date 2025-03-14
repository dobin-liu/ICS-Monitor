package com.bi.config;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bi.base.model.CipherWrapper;
import com.bi.base.model.Percent;
import com.bi.common.handler.BaseResponseErrorHandler;
import com.bi.common.interceptor.BaseClientHttpRequestInterceptor;
import com.bi.spring.rest.client.DefaultWebserviceClient;
import com.bi.spring.rest.converter.BiDateFormat;
import com.bi.spring.rest.jackson2.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
public class WebAppConfigurer implements WebMvcConfigurer {
    
    @Autowired
    private Properties myProperites; // --注入資料庫來源參數讀取 bean 物件，讓參數可先讀取完畢

    
    private RestTemplate restTemplateSetting(RestTemplate template) {

        final List<HttpMessageConverter<?>> cs = new ArrayList<HttpMessageConverter<?>>();// 全部需要的Converter加入後會判斷選取對應的Converter
        cs.add(stringConverter());
        cs.add(formHttpMessageConverter());
        cs.add(jsonConverter());
        template.setMessageConverters(cs);

        final BaseResponseErrorHandler handler = new BaseResponseErrorHandler();
        template.setErrorHandler(handler);

        final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        // interceptors.add(insertAdmSysWsLogsInterceptor()); // 儲存串接紀錄服務
        interceptors.add(baseClientHttpRequestInterceptor());
        template.setInterceptors(interceptors);
        return template;
    }

    @Bean
    public ClientHttpRequestInterceptor baseClientHttpRequestInterceptor() {
        return new BaseClientHttpRequestInterceptor(); //
    }

    @Bean(name = "bi.dateFormat")
    public BiDateFormat biDateFormat() {
        return new BiDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        final ObjectMapper om = new ObjectMapper();
        om.setDateFormat(biDateFormat());

        // JacksonUtil.addSerializer(om, new JacksonUtil.DateSerializer(biDateFormat()), "mapperModule");

        final SimpleModule module = new SimpleModule();
        module.addSerializer(new JacksonUtil.DateSerializer(biDateFormat()));
        module.addSerializer(new JacksonUtil.PercentSerializer(Percent.class));
        module.addDeserializer(Percent.class, new JacksonUtil.PercentDeserializer(Percent.class));

        module.addSerializer(new JacksonUtil.CipherWrapperSerializer(CipherWrapper.class));
        module.addDeserializer(CipherWrapper.class, new JacksonUtil.CipherWrapperDeserializer(CipherWrapper.class));
        om.registerModule(module);

        om.setVisibility(PropertyAccessor.ALL, Visibility.NONE);// turn all off 先關掉全部
        om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);// turn on only field 再只開啟field
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 若遇到不在物件欄位裡的則不處理

        return om;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        final MappingJackson2HttpMessageConverter j2h = new MappingJackson2HttpMessageConverter(jsonObjectMapper());
        return j2h;
    }

    @Bean
    public HttpMessageConverter<String> stringConverter() {// 預設CHARSET = Charset.forName("ISO-8859-1")
        final StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return converter;
    }

    @Bean
    public FormHttpMessageConverter formHttpMessageConverter() {
        final FormHttpMessageConverter converter = new FormHttpMessageConverter();
        return converter;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // --開啟 針對靜態資源資料夾下的讀取
        registry.addResourceHandler("/publicstatic/**").addResourceLocations("classpath:/static/");
        // registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
        // .resourceChain(false);
        // registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
      registry.addRedirectViewController("/swagger-ui.html", "/swagger-ui/index.html").setStatusCode(HttpStatus.PERMANENT_REDIRECT);
      registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html").setStatusCode(HttpStatus.PERMANENT_REDIRECT);
      registry.addRedirectViewController("/swagger-ui/", "/swagger-ui/index.html").setStatusCode(HttpStatus.PERMANENT_REDIRECT);
    }
}
