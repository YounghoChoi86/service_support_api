package com.tmoncorp.support.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tmoncorp.support.domain.Support;
import com.tmoncorp.support.domain.SupportBulkInfo;
import com.tmoncorp.support.repository.SupportRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ValidationException;
import java.io.*;
import java.util.*;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
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
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    @Autowired
    private WebApplicationContext context;

    private static int index = 0;

    private static final List<String> institutes =
            Arrays.asList(new String[]{"주택도시기금", "국민은행", "우리은행", "신한은행", "한국시티은행",
            "하나은행", "농협은행/수협은행", "외환은행", "기타은행"});

    private static final int MIN_RECORD_VALUES_LENGTH = 11;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document("{method-name}/{class-name}"))
                .build();
    }

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
            log.error("filen not found = {}", e);
            throw e;
        } catch (IOException e) {
            log.error("io exception occur = {}", e);
            throw e;
        } catch (ValidationException e) {
            log.error("ValidationException occur = {}", e);
            throw e;
        }

        long startMillis = System.currentTimeMillis();

        int requestCount = 0;

        for (SupportBulkInfo supportBulkInfo : supportBulkInfos) {
            String jsonSupportBulkbody = objectMapper.writeValueAsString(supportBulkInfo);
            ResultActions resultActions = mockMvc.perform(post("/supports/bulk").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonSupportBulkbody)).andExpect(status().isCreated());
            if (requestCount == 0) {
                resultActions
                        .andDo(document("support" + index++));
            }
            requestCount++;
        }
        long endMillis = System.currentTimeMillis();

        log.info("elapsed time = {}ms requestCount = {}", endMillis - startMillis, requestCount);

        //레코드 카운트 검증 ()  -> 154 라인 * 은행 갯수 9 = 1386

        long countOfSupports = supportRepository.count();
        long expectedSupportRecordCount = 1386;

        Assert.assertEquals(expectedSupportRecordCount, countOfSupports);
    }



    private SupportBulkInfo convertCsvRecordToSupportBulInfo(CSVRecord csvRecord) throws ValidationException {

        log.info("csvRecord length= {}", csvRecord.size());
        if (csvRecord.size() < MIN_RECORD_VALUES_LENGTH) {
            throw new ValidationException(csvRecord.toString());
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
                .andDo(document("support" + index++))
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
                .andExpect(jsonPath("amount").value(support.getAmount()))
                .andDo(document("support" + index++));
    }

    @Test
    public void 연도_월_기관에_해당하는_지원금_생성_잘못된_년도입력_400_ERROR() throws Exception {
        Support support = new Support();
        support.setYear(0);
        support.setMonth(10);
        support.setBank("외환은행");
        support.setAmount(1000);
        String jsonBody = objectMapper.writeValueAsString(support);
        String responseBody = mockMvc.perform(post("/supports").contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);

        support.setYear(10000);
        support.setMonth(1);
        support.setBank("외환은행");
        support.setAmount(1000);
        jsonBody = objectMapper.writeValueAsString(support);
        responseBody = mockMvc.perform(post("/supports").contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);
    }

    @Test
    public void 연도_월_기관에_해당하는_지원금_생성_잘못된_월_입력_400_ERROR() throws Exception {
        Support support = new Support();
        support.setYear(2018);
        support.setMonth(0);
        support.setBank("외환은행");
        support.setAmount(1000);
        String jsonBody = objectMapper.writeValueAsString(support);
        String responseBody = mockMvc.perform(post("/supports").contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);

        support.setYear(2018);
        support.setMonth(13);
        support.setBank("외환은행");
        support.setAmount(1000);
        jsonBody = objectMapper.writeValueAsString(support);
        responseBody = mockMvc.perform(post("/supports").contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);
    }

    @Test
    public void 연도_월_기관에_해당하는_지원금_생성_잘못된_은행명_입력_400_ERROR() throws Exception {
        Support support = new Support();
        support.setYear(2018);
        support.setMonth(10);
        support.setBank(""); //EMPTY STRING
        support.setAmount(1000);
        String jsonBody = objectMapper.writeValueAsString(support);
        String responseBody = mockMvc.perform(post("/supports")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);

        support.setYear(2018);
        support.setMonth(10);
        support.setBank("\t \t"); //WHITE_SPACE_STRING
        support.setAmount(1000);
        jsonBody = objectMapper.writeValueAsString(support);
        responseBody = mockMvc.perform(post("/supports")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);
    }

    @Test
    public void 연도_월_기관에_해당하는_지원금_생성_잘못된_지원금입력() throws Exception {
        Support support = new Support();
        support.setYear(2018);
        support.setMonth(10);
        support.setBank("외환은행"); //EMPTY STRING
        support.setAmount(-1);
        String jsonBody = objectMapper.writeValueAsString(support);
        String responseBody = mockMvc.perform(post("/supports").contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++))
                .andReturn().getResponse().getContentAsString();

        log.info("responseBody=[{}]", responseBody);
    }

    @Test
    public void 연도_월_기관에_해당하는_지원금_삭제_404_ERROR() throws Exception {
        mockMvc.perform(delete("/supports/" + "testidkk")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("message").isString())
                .andDo(document("support" + index++));
    }

    @Test
    public void 연도_지원금_정보_API테스트() throws Exception  {
        mockMvc.perform(get("/supports/totalsOfYears"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("주택기금정보"))
                .andExpect(jsonPath("supports_of_years").exists())
                .andDo(document("support" + index++));
    }

    @Test
    public void 외환은행_최대_최소_지원금액_API_테스트() throws Exception {
        String bankName = "외환은행";
        mockMvc.perform(get("/supports/amountMinMax?bank=" + bankName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("bank").value(bankName))
                .andDo(document("support" + index++));
    }

    @Test
    public void 최대_지원금액_API_테스트() throws Exception {
        mockMvc.perform(get("/supports/topAmountBankOfYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("year").exists())
                .andExpect(jsonPath("bank").exists())
                .andDo(document("support" + index++));
    }
}
