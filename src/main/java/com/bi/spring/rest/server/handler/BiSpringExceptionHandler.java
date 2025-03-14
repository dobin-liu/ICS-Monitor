package com.bi.spring.rest.server.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import com.bi.base.config.AppStaticConfig;
import com.bi.spring.rest.exception.BIRestException;
import com.bi.spring.rest.model.ErrorEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mingfu
 * @version
 *
 */
@Slf4j
@ControllerAdvice
public class BiSpringExceptionHandler {

//    private static final String NEW_LINE = "\r\n";
//    private static final String PADDING = "    ";
    private final int maxLogBodyChars;

    public BiSpringExceptionHandler() {
        this(5000);
    }

    public BiSpringExceptionHandler(int maxLogBodyChars) {
        this.maxLogBodyChars = maxLogBodyChars;
    }

    // http://www.iana.org/assignments/http-status-codes/http-status-codes.xml
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorEntity> defaultErrorHandler(HttpServletRequest req, Exception e, WebRequest request)
            throws WSException, RestException, ResourceAccessException, RuntimeException {

        log.info(req.getClass().getCanonicalName());
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            // throw e;
            if (e instanceof RestException) {
                throw (RestException) e;
            }
            else if (e instanceof WSException) {
                throw (WSException) e;
            }
            else if (e instanceof ResourceAccessException) {
                throw (ResourceAccessException) e;
            }
            else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

        }

//        String logStr = createLogString(req);
//        log.error(MecUtil.toLogStr(logStr));

        final HttpStatus hs;
        final String statusCode;
        final String message;
        if (e instanceof BIRestException) {
            BIRestException re = (BIRestException) e;
            hs = re.getStatus();
            statusCode = re.getStatusCode();
        }
        else {
            hs = getDefaultErrorStatus();
            statusCode = String.valueOf(hs.value());
        }

        if (e instanceof HttpMessageNotReadableException && !AppStaticConfig.isProfileDev() && !AppStaticConfig.isProfileLsit()) {
            message = "unparseable data, please check data format";
        }
        else {
            message = e.getMessage();
        }

        return new ResponseEntity<ErrorEntity>(new ErrorEntity(statusCode, message), hs);
    }

    protected HttpStatus getDefaultErrorStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

//    private String createLogString(HttpServletRequest req) {
//
//        StringBuilder logSb = new StringBuilder(NEW_LINE);
//        final String url = req.getRequestURI();
//        logSb.append("url: ").append(url).append(MecUtil.toLogStr(NEW_LINE));
//        final String method = req.getMethod();
//        logSb.append("method: ").append(method).append(MecUtil.toLogStr(NEW_LINE));
//        final String contentType = req.getContentType();
//        logSb.append("content-type: ").append(contentType).append(MecUtil.toLogStr(NEW_LINE));
//        logSb.append("accept: ").append(req.getHeader("Accept")).append(MecUtil.toLogStr(NEW_LINE));
//        if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType) || "multipart/form-data".equalsIgnoreCase(contentType)) {
//
//            logSb.append("parameters: ").append(NEW_LINE);
//
//            @SuppressWarnings("unchecked")
//            Map<String, ?> pmap = req.getParameterMap();
//
//            Object val;
//            for (Entry<String, ?> entry : pmap.entrySet()) {
//                logSb.append(PADDING).append(entry.getKey()).append(": ");
//                val = entry.getValue();
//                if (val instanceof Object[]) {
//                    logSb.append(Arrays.toString((Object[]) val));
//                }
//                else {
//                    logSb.append(val);
//                }
//                logSb.append(NEW_LINE);
//
//            }
//        }
//        else {
//            logSb.append("query string: ").append(req.getQueryString()).append(NEW_LINE);
//
//            logSb.append("request body: ").append(NEW_LINE);
//
//            if ("GET".equals(method) || "DELETE".equals(method)) {
//                logSb.append("--N/A--").append(NEW_LINE);
//            }
//            else {
//                int chars = 0;
//                try {
//
//                    BufferedReader reader = req.getReader();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        if (logSb.length() >= maxLogBodyChars) {
//                            break;
//                        }
//                        logSb.append(PADDING).append(line).append(NEW_LINE);
//                        chars += line.length();
//                    }
//
//                }
//                catch (IOException e) {
//                    log.error("HttpServletRequest reader readLine error: " + MecUtil.SafeErrorLog(e));
//                }
//            }
//        }
//        return logSb.toString();
//    }

}
