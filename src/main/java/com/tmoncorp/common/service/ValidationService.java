package com.tmoncorp.common.service;

import com.tmoncorp.common.exception.ValidationException;
import org.springframework.util.StringUtils;

public abstract class ValidationService {
    public void checkStringParamWidthName(String name, String param) throws ValidationException {
        if (StringUtils.isEmpty(param)) {
            throw new ValidationException("허용되지 않는 값입니다. " + name + " : " + param);
        }
        if (StringUtils.isEmpty(param.trim())) {
            throw new ValidationException("허용되지 않는 값입니다.  " + name + " : " + param);
        }
    }
}
