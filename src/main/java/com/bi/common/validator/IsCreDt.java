package com.bi.common.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Constraint(validatedBy = IsCreDtValidator.class) // 指定我們自定義的校驗類
public @interface IsCreDt {

    /**
     * 是否強制校驗
     * 
     * @return 是否強制校驗的boolean值
     */
    boolean required() default false;

    /**
     * 校驗不通過時的報錯資訊
     * 
     * @return 校驗不通過時的報錯資訊
     */
    String message() default "日期時間格式錯誤";

    /**
     * 將validator進行分類，不同的類group中會執行不同的validator操作
     * 
     * @return validator的分類型別
     */
    Class<?>[] groups() default {};

    /**
     * 主要是針對bean，很少使用
     * 
     * @return 負載
     */
    Class<? extends Payload>[] payload() default {};

}

