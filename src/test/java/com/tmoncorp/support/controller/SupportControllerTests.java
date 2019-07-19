package com.tmoncorp.support.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SupportControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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
