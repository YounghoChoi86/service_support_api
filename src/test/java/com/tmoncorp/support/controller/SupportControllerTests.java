package com.tmoncorp.support.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.tmoncorp.support.domain.Support;
import com.tmoncorp.support.domain.SupportBulkInfo;
import com.tmoncorp.support.repository.SupportRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.io.*;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SupportControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SupportRepository supportRepository;

    private static final List<String> institutes =
            Arrays.asList(new String[]{"주택도시기금", "국민은행", "우리은행", "신한은행", "한국시티은행",
            "하나은행", "농협은행/수협은행", "외환은행", "기타은행"});

    private static final int MIN_RECORD_VALUES_LENGTH = 11;

    @Test
    public void CSV_레코드에_해당하는_형식의_CREATE_API_호출() throws Exception {
        // 이전 테스트 데이터 삭제 처리
        supportRepository.deleteAll();
        List<SupportBulkInfo> supportBulkInfos = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data_supports.csv").getFile());
        try {
            Reader csvReader = new FileReader(file.getAbsoluteFile());

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(csvReader);

            for (CSVRecord csvRecord : records) {
                SupportBulkInfo supportBulkInfo = null;
                try {
                    supportBulkInfo = convertCsvRecordToSupportBulInfo(csvRecord);
                    supportBulkInfos.add(supportBulkInfo);
                } catch (NumberFormatException e) {
                    log.error("부절적한 레코드 skip : {}", csvRecord.toString());
                    continue;
                }
                log.info("supportBulkInfo = {}", supportBulkInfo);
            }
            //exception이 발생하지 않은 파일에서만 각 레코에드에 대한 /supports/bulk POST 메소드를 통한 생성 처리

        } catch (FileNotFoundException e) {
            log.error("filen not found", e);
        } catch (IOException e) {
            log.error("io exception occur", e);
        } catch (InvalidArgumentException e) {
            log.error("InvalidArgumentException occur", e);
        }

        long startMillis = System.currentTimeMillis();

        int requestCount = 0;

        for (SupportBulkInfo supportBulkInfo : supportBulkInfos) {
            String jsonSupportBulkbody = objectMapper.writeValueAsString(supportBulkInfo);
            mockMvc.perform(post("/supports/bulk").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonSupportBulkbody)).andExpect(status().isCreated());
            requestCount++;
        }
        long endMillis = System.currentTimeMillis();

        log.info("elapsed time = {}ms requestCount = {}", endMillis - startMillis, requestCount);

        //레코드 카운트 검증 ()  -> 154 라인 * 은행 갯수 9 = 1386

        long countOfSupports = supportRepository.count();
        long expectedSupportRecordCount = 1386;

        Assert.assertEquals(expectedSupportRecordCount, countOfSupports);
    }

    private SupportBulkInfo convertCsvRecordToSupportBulInfo(CSVRecord csvRecord) throws InvalidArgumentException {

        log.info("csvRecord length= {}", csvRecord.size());
        if (csvRecord.size() < MIN_RECORD_VALUES_LENGTH) {
            throw new InvalidArgumentException(new String[]{ csvRecord.toString() });
        }

        SupportBulkInfo supportBulkInfo = new SupportBulkInfo();

        supportBulkInfo.setYear(Integer.parseInt(csvRecord.get(0)));
        supportBulkInfo.setMonth(Integer.parseInt(csvRecord.get(1)));

        int institutesLength = institutes.size();
        Map<String, Long> detailAmount = new TreeMap<>();
        for (int i = 0; i < institutesLength; i++) {
            //"2,000" 같은 케이스를 처리하기 위해 ,를 제거 한다
            String value = csvRecord.get(i + 2).replaceAll(",", "");
            detailAmount.put(institutes.get(i), Long.parseLong(value));
        }
        supportBulkInfo.setDetailAmount(detailAmount);

        return supportBulkInfo;
    }

    @Test
    public void 연도_월_기관에_해당하는_지원금_생성_삭제() throws Exception {
        Support support = new Support();
        support.setYear(2080);
        support.setMonth(10);
        support.setBank("외환은행");
        support.setAmount(1000);
        String jsonBody = objectMapper.writeValueAsString(support);
        String responseBody = mockMvc.perform(post("/supports").contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody)).andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);

        //삭제
        Support createdSupport = objectMapper.readValue(responseBody, Support.class);

        mockMvc.perform(delete("/supports/" + createdSupport.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(createdSupport.getId()))
                .andExpect(jsonPath("year").value(support.getYear()))
                .andExpect(jsonPath("month").value(support.getMonth()))
                .andExpect(jsonPath("bank").isString())
                .andExpect(jsonPath("amount").value(support.getAmount()));
    }

    @Test
    public void 연도_월_기관에_해당하는_지원금_삭제_404_ERROR() throws Exception {
        mockMvc.perform(delete("/supports/" + "testidkk"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("message").isString());
    }

    @Test
    public void 연도_지원금_정보_API테스트() throws Exception  {
        mockMvc.perform(get("/supports/totalsOfYears"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("주택기금정보"))
                .andExpect(jsonPath("supports_of_years").exists());
    }

    @Test
    public void 외환은행_최대_최소_지원금액_API_테스트() throws Exception {
        String bankName = "외환은행";
        mockMvc.perform(get("/supports/amountMinMax?bank=" + bankName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("bank").value(bankName));
    }

    @Test
    public void 최대_지원금액_API_테스트() throws Exception {
        mockMvc.perform(get("/supports/topAmountBankOfYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("year").exists())
                .andExpect(jsonPath("bank").exists());
    }

}
