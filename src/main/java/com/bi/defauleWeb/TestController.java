
package com.bi.defauleWeb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bi.common.dao.TblSysApiLogsDao;
import com.bi.common.model.FileUploadReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 測試API
 * 
 * @version 1.0.0
 *
 */
@RestController
@Api(tags = {"測試API 方法"})
@RequestMapping("/public2")
@Slf4j
public class TestController {
    
    @Autowired
    private TblSysApiLogsDao tblSysApiLogsDao;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(new String[] {});
    }
    
    @ApiOperation(value = "檔案轉base64")
    @PostMapping(path = "/fileToBase64", 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart(value = "req", required = false)  String req, @RequestPart("file") MultipartFile file) throws JsonMappingException, JsonProcessingException {
        if (req != null) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            FileUploadReq fileUplodadReq = mapper.readValue(req, FileUploadReq.class);
        }
        
        final long maxSize = 10485760;
        final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!("png".equals(extension) || "jpg".equals(extension) || "jpeg".equals(extension))) {
            // throw new IllegalArgumentException("File type error!");
        }
        if (file.getSize() > maxSize) {
            // throw new IllegalArgumentException("File size too large!");
        }

        String ret = "";
        InputStream is = null;
        try {
            is = file.getInputStream();
            
            ret = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
        }
        catch (IOException e) {
            log.error("genFileBase64", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    log.error("close is", e);
                }
            }
        }
        
        // return new ResponseEntity<String>(ret, HttpStatus.OK);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bs64.txt");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(ret.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(ret);
    }
    
    @ApiOperation(value = "測試 db")
    @GetMapping("/testDb")
    public Map<String, Object> testDb() throws IOException {
        Map<String, Object> rt = new HashMap<String, Object>();
        
        int n = tblSysApiLogsDao.getRowsCount();
        
        rt.put("msg", n);
        rt.put("result", "OK");
        return rt;
    }

}
