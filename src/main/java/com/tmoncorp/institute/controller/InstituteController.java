package com.tmoncorp.institute.controller;

import com.tmoncorp.institute.domain.Institute;
import com.tmoncorp.institute.exception.InvalidArgumentException;
import com.tmoncorp.institute.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/institutes")
public class InstituteController {
    @Autowired
    private InstituteService instituteService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Institute createInstitute(@RequestBody Institute institute) throws Exception {
        return instituteService.createInstitute(institute);
    }

    @DeleteMapping("/{instituteNo}")
    public Institute deleteInstitute(@PathVariable String instituteNo) throws Exception {
        return instituteService.deleteInstitute(instituteNo);
    }
    @GetMapping("")
    public List<Institute> getAllInstitute() {
        return instituteService.getAllInstitute();
    }
    @GetMapping("/{instituteCode}")
    public Institute getInstitute(@PathVariable String instituteCode) throws Exception {
        return instituteService.getInstituteByInstituteCode(instituteCode);
    }


    @PutMapping("/{instituteCode}")
    public Institute updateInstitute(@PathVariable String instituteCode, @RequestBody Institute institute) throws Exception {
        institute.setInstituteCode(instituteCode);
        return instituteService.updateInstitute(institute);
    }
}
