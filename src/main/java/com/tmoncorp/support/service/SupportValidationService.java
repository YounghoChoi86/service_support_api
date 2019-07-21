package com.tmoncorp.support.service;

import com.tmoncorp.common.exception.ValidationException;
import com.tmoncorp.common.service.ValidationService;
import com.tmoncorp.support.domain.Support;
import com.tmoncorp.support.domain.SupportBulkInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Slf4j
@Service
public class SupportValidationService extends ValidationService {
    public static final int MIN_YEAR = 1;
    public static final int MAX_YEAR = 9999;
    public static final int MIN_MONTH = 1;
    public static final int MAX_MONTH = 12;

    public void checkSupport(Support support) throws ValidationException {
        checkYear(support.getYear());
        checkMonth(support.getMonth());
        checkAmount(support.getAmount());
        checkBank(support.getBank());
    }

    public void checkSupportBulkInfo(SupportBulkInfo supportBulkInfo) throws ValidationException {
        checkYear(supportBulkInfo.getYear());
        checkMonth(supportBulkInfo.getMonth());

        Map<String, Long> detailAmount = supportBulkInfo.getDetailAmount();
        if (CollectionUtils.isEmpty(detailAmount)) {
            throw new ValidationException("유효하지 않은 값입니다. detail_amount:" + detailAmount);
        }
        long amount = 0;
        for (String bank : detailAmount.keySet()) {
            checkBank(bank);
            amount = detailAmount.get(bank);
            checkAmount(amount);
        }
    }

    public void checkYear(int year) throws ValidationException {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new ValidationException("유효하지 않은 연도입니다. year:" + year);
        }
    }

    public void checkMonth(int month) throws ValidationException {
        if (month < MIN_MONTH || month > MAX_MONTH) {
            throw new ValidationException("유효하지 않은 월입니다. month:" + month);
        }
    }

    public void checkAmount(long amount) throws ValidationException{
        if (amount < 0L || Long.MAX_VALUE < amount) {
            throw new ValidationException("유효하지 않은 지원금 값입니다. amount:" + amount);
        }
    }

    public void checkBank(String bank) throws ValidationException {
        checkStringParamWidthName("bank", bank);
    }
}
