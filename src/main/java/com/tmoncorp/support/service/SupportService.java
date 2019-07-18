package com.tmoncorp.support.service;

import com.tmoncorp.institute.domain.Institute;
import com.tmoncorp.institute.exception.InstituteNotFoundException;
import com.tmoncorp.institute.repository.InstituteRepository;
import com.tmoncorp.support.domain.*;
import com.tmoncorp.support.exception.SupportNotFouncException;
import com.tmoncorp.support.repository.SupportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
public class SupportService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private InstituteRepository instituteRepository;

    public Support createSupport(Support support) throws InstituteNotFoundException{
        Optional<Institute> optionalInstitute
                = instituteRepository.findFirstByInstituteName(support.getBank());
        if (false == optionalInstitute.isPresent()) {
            throw new InstituteNotFoundException(support.getBank());
        }
        support.setBank(optionalInstitute.get().getInstituteCode());

        return supportRepository.insert(support);
    }

    public List<Support> createSupportBulkInfo(SupportBulkInfo supportBulkInfo) throws InstituteNotFoundException {

        Map<String, Long> detailAmountMap = supportBulkInfo.getDetailAmount();
        List<Support> supports = new ArrayList<>(detailAmountMap.size());
        int year = supportBulkInfo.getYear();
        int month = supportBulkInfo.getMonth();

        log.debug("year = {}", supportBulkInfo.getYear());
        log.debug("month = {}", supportBulkInfo.getMonth());
        log.debug("detailAmount={}", detailAmountMap);


        for (String bankName : detailAmountMap.keySet()) {
            Optional<Institute> instituteOptional =
                    instituteRepository.findFirstByInstituteName(bankName);
            Institute institute =
                    instituteOptional.orElseThrow(() -> new InstituteNotFoundException(bankName));
            Support support = new Support();
            support.setYear(year);
            support.setMonth(month);
            support.setBank(institute.getInstituteCode());
            support.setAmount(detailAmountMap.get(bankName));

            supports.add(support);
        }

        return supportRepository.insert(supports);
    }

    public SupportsOfYears getSupportsOfYears() {
        //TODO MongoRepository로 옮길 것..
        Aggregation agg =
                newAggregation(group("year", "bank").sum("amount").as("amount"),
                        sort(Sort.Direction.DESC, "year", "bank"));

        AggregationResults<Support> groupAmounts = mongoTemplate
                .aggregate(agg, Support.class, Support.class);

        List<Support> amountSumOfYearBank = groupAmounts.getMappedResults();

        Map<Integer, List<Support>> supportMap = new TreeMap<>();

        amountSumOfYearBank.forEach(e -> {
            List<Support> supportsOfYear = supportMap.get(e.getYear());
            log.info("Support={}", e);
            if (null == supportsOfYear) {
                supportsOfYear = new ArrayList<>();
                supportsOfYear.add(e);
                supportMap.put(e.getYear(), supportsOfYear);

            }
            else {
                supportsOfYear.add(e);
            }
        });
        //TODO ProxyCash Service
        List<Institute> instituteList = instituteRepository.findAll();
        final Map<String, String> instituteCodeToNameMap
                = instituteList.stream().collect(Collectors.toMap(Institute::getInstituteCode, Institute::getInstituteName));
        log.info("supportMap={}", supportMap);

        List<SupportsOfYear> supportsOfYearList =
                supportMap.keySet().stream()
                        .map(e -> supportMap.get(e))
                        .map(e -> {
                            SupportsOfYear supportsOfYear = new SupportsOfYear();
                            long totalAmount = e.stream().mapToLong(k -> k.getAmount()).sum();
                            Map<String, Long>  detailAmount = e.stream()
                                    .collect(Collectors.toMap(entity -> instituteCodeToNameMap.get(entity.getBank()), Support::getAmount));
                            supportsOfYear.setTotalAmount(totalAmount);
                            supportsOfYear.setDetailAmount(detailAmount);
                            supportsOfYear.setYear(e.get(0).getYear());
                            log.debug("supportsOfYear={}", supportsOfYear);
                            return supportsOfYear;
                }).collect(Collectors.toList());

        SupportsOfYears supportsOfYears = new SupportsOfYears();
        supportsOfYears.setSupportsOfYears(supportsOfYearList);

        return supportsOfYears;
    }

    public TopAmountBankOfYear getTopBankOfYear() throws SupportNotFouncException, InstituteNotFoundException{
        //TODO MongoRepository로 옮길 것..
        Aggregation agg =
                newAggregation(group("year", "bank").sum("amount").as("amount"),
                        sort(Sort.Direction.DESC, "year", "bank"));

        AggregationResults<Support> groupAmounts = mongoTemplate
                .aggregate(agg, Support.class, Support.class);

        List<Support> amountSumOfYearBank = groupAmounts.getMappedResults();

        Support support =  amountSumOfYearBank.stream().max((e1, e2)-> {
            if (e1.getAmount() == e2.getAmount()) {
                return 0;
            }
            if (e1.getAmount() < e2.getAmount()) {
                return -1;
            }
            return 1;
        }).orElseThrow(() -> new SupportNotFouncException("최대 금액을 찾을 수 없습니다."));

        Institute institute =
                instituteRepository.findById(support.getBank())
                .orElseThrow(() -> new InstituteNotFoundException(support.getBank()));
        log.info("maxAmountOfYear={}", support.getAmount());

        return new TopAmountBankOfYear(support.getYear(), institute.getInstituteName());
    }
}
