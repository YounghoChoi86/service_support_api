package com.tmoncorp.institute;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmoncorp.institute.domain.Institute;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InstituteApplicationTests {
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	private static final String TEST_INSTITUTE_NAME = "테스트은행";

	@Test
	public void 기관_등록_API_CRUD_테스트() throws Exception {
		//생성
		mockMvc.perform(post("/institutes").contentType(MediaType.APPLICATION_JSON).content(
				"{\"instituteName\":\"" + TEST_INSTITUTE_NAME +  "\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("instituteCode").exists())
				.andExpect(jsonPath("instituteName").value(TEST_INSTITUTE_NAME));

		ResultActions resultActions = mockMvc.perform(get("/institutes"));
		MvcResult mvcResult = resultActions.andReturn();

		resultActions.andExpect(status().isOk());

		List<Institute> instituteList = objectMapper
					.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Institute>>(){});
		Institute targetInstitute =
				instituteList.stream().filter(e -> e.getInstituteName().equals(TEST_INSTITUTE_NAME)).findFirst().orElse(null);

		Assert.assertEquals(TEST_INSTITUTE_NAME, targetInstitute.getInstituteName());

		mockMvc.perform(put("/institutes/" + targetInstitute.getInstituteCode()).contentType(MediaType.APPLICATION_JSON).content(
				"{\"instituteName\":\"" + TEST_INSTITUTE_NAME + 2 + "\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("instituteCode").exists())
				.andExpect(jsonPath("instituteName").value(TEST_INSTITUTE_NAME + 2));

		//삭제 시에 update 확인(instituteName : "테스트은행")
		mockMvc.perform(delete("/institutes/" + targetInstitute.getInstituteCode()).contentType(MediaType.APPLICATION_JSON).content(
				"{\"instituteName\":\"" + TEST_INSTITUTE_NAME + "\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("instituteCode").exists())
				.andExpect(jsonPath("instituteName").value(TEST_INSTITUTE_NAME + 2));

	}
}
