package com.wfg.validate;



import com.wfg.utils.UrlUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckUrlValidate implements ConstraintValidator<CheckUrl, String> {

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return UrlUtil.verifyUrl(url.trim());
    }
}
