
package com.bi.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bi.common.service.ApiManager;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(tags = {"API server"})
@RequestMapping("/api")
@Slf4j
public class ApiController {
    
    @Autowired
    private ApiManager apiManager;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(new String[] {});
        
        final String objectName = binder.getObjectName();
        /*
        if (GetCustPolReq.class.equals(binder.getTarget().getClass())) {
            binder.addValidators(getCustPolGoValidator);
        }
        else if (OcrReq.class.equals(binder.getTarget().getClass())) {
            binder.addValidators(ocrReqValidator);
        }
        */
    }
    
    
    
}
