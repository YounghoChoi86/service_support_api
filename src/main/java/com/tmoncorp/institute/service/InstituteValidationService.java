package com.tmoncorp.institute.service;


import com.tmoncorp.common.exception.ValidationException;
import com.tmoncorp.common.service.ValidationService;
import com.tmoncorp.institute.domain.Institute;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class InstituteValidationService extends ValidationService {

    public void checkInstitute(Institute institute) throws ValidationException {
        if (Objects.isNull(institute)) {
            throw new ValidationException("body는 빈 값이 될 수 없습니다");
        }
        checkInstituteCode(institute.getInstituteCode());
        checkInstituteCode(institute.getInstituteName());
    }

    public void checkInstituteName(String instituteName) throws ValidationException {
        checkStringParamWidthName("instituteName", instituteName);
    }

    public void checkInstituteCode(String instituteCode) throws ValidationException {
        checkStringParamWidthName("instituteCode", instituteCode);
    }
}
