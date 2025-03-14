package com.bi.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileUploadReq   {
    @JsonProperty(value = "type")
    @ApiModelProperty(value="類型")
    private String type;
    
    
}
