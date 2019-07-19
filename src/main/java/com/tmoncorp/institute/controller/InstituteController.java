package com.tmoncorp.institute.controller;

import com.tmoncorp.common.exception.ValidationException;
import com.tmoncorp.institute.domain.Institute;
import com.tmoncorp.institute.service.InstituteService;
import com.tmoncorp.institute.service.InstituteValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/institutes")
public class InstituteController {
    @Autowired
    private InstituteService instituteService;
    @Autowired
    private InstituteValidationService instituteValidationService;

    @GetMapping("")
    public List<Institute> getAllInstitute() {
        return instituteService.getAllInstitute();
    }

    @GetMapping("/{instituteCode}")
    public Institute getInstitute(@PathVariable String instituteCode) throws Exception {
        instituteValidationService.checkInstituteCode(instituteCode);
        return instituteService.getInstituteByInstituteCode(instituteCode);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Institute createInstitute(@RequestBody Institute institute) throws Exception {
        instituteValidationService.checkInstituteName(institute.getInstituteName());
        return instituteService.createInstitute(institute);
    }

    //TODO 기관 삭제 시에 관련된 레코드도 모두 supports에 존재하는 레코드도 삭제하도록 해야함..-_ㅠ
    //mongodb는 casecade 삭제 안되나유?~
    @DeleteMapping("/{instituteCode}")
    public Institute deleteInstitute(@PathVariable String instituteCode) throws Exception {
        instituteValidationService.checkInstituteCode(instituteCode);
        return instituteService.deleteInstitute(instituteCode);
    }

    @PutMapping("/{instituteCode}")
    public Institute updateInstitute(@PathVariable String instituteCode, @RequestBody Institute institute) throws Exception {
        instituteValidationService.checkInstitute(institute);
        if (!instituteCode.equals(institute.getInstituteCode())) {
            throw new ValidationException("InstituteCode는 다를 수 없습니다. "+ instituteCode + "!=" + institute.getInstituteCode());
        }
        return instituteService.updateInstitute(institute);
    }
}
