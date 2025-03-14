package com.bi.common.validator;

import java.text.ParseException;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.bi.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsCreDtValidator implements ConstraintValidator<IsCreDt, String> {

    // 是否強制校驗
    private boolean required;

    @Override
    public void initialize(IsCreDt constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // TODO Auto-generated method stub
        try {
            final String format = "yyyy/MM/dd HH:mm";
            final Date date = DateUtil.parseDate(value, format);
            final String dateStr = DateUtil.format(date, format);
            if (dateStr != null && dateStr.equals(value)) {
                return true;
            }
        }
        catch (ParseException e) {
            // return false;
            log.error("isValid");
        }
        return false;
    }
    
}
