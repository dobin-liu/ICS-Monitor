package com.bi.common.validator;

import java.text.ParseException;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.bi.util.DateUtil;
import com.bi.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsCustDateValidator implements ConstraintValidator<IsCustDate, String> {

    // 是否強制校驗
    private boolean required;

    @Override
    public void initialize(IsCustDate constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isEmpty(value)) {
            if (!required) {
                return true;
            }
            return false;
        }
        try {
            final Date date = DateUtil.parseDate(value, DateUtil.PATTERN_YYYY_MM_DD);
            final String dateStr = DateUtil.format(date, DateUtil.PATTERN_YYYY_MM_DD);
            if (dateStr != null && dateStr.equals(value)) {
                return true;
            }
        }
        catch (ParseException e) {
            // return false;
            log.error("isValid: parse date error");
        }
        return false;
    }
    
}
