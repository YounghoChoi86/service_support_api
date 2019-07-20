package com.tmoncorp.support.controller;

import com.tmoncorp.support.domain.*;
import com.tmoncorp.support.service.SupportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/supports")
public class SupportController {
    @Autowired
    private SupportService supportService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Support createSupport(@RequestBody Support support) throws Exception {
        log.debug("Support=({})", support);
        return supportService.createSupport(support);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bulk")
    public List<Support> createSupportInfos(@RequestBody SupportBulkInfo supportBulkInfo) throws Exception {
        log.debug("SupportBulkInfo=()", supportBulkInfo);
        return supportService.createSupportBulkInfo(supportBulkInfo);
    }

    @DeleteMapping("/{supportId}")
    public Support deleteSupport(@PathVariable String supportId) throws Exception {
        log.debug("supportId = ({})", supportId);
        return supportService.deleteSupport(supportId);
    }

    @GetMapping("/totalsOfYears")
    public SupportsOfYears getSupportsOfYears() throws Exception {
        SupportsOfYears supportsOfYears = supportService.getSupportsOfYears();
        log.info("before return response= {}", supportsOfYears);
        return supportsOfYears;
    }

    @GetMapping("/topAmountBankOfYear")
    public TopAmountBankOfYear getTopBankOfYear() throws Exception {
        return supportService.getTopBankOfYear();
    }

    @GetMapping("/amountMinMax")
    public AmountMinMaxOfBank getAmountOfMinMaxOfBank(@RequestParam String bank) throws Exception {
        return supportService.getAmountMinMaxOfBank(bank);
    }
}
