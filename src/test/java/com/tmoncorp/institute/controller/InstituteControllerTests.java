package com.tmoncorp.institute.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmoncorp.institute.domain.Institute;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class InstituteControllerTests {
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
	@Autowired
	private WebApplicationContext context;

	private static final String TEST_INSTITUTE_NAME = "테스트은행";
	private static final String DUPLICATED_INSTITUTE_NAME = "외환은행";
	private static final String NOT_EXIST_INSTITUTE_CODE = "TEST444444000";
	private static int index = 0;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.alwaysDo(document("{method-name}/{class-name}"))
				.build();
	}
	@Test
	public void 기관_등록_API_CRUD_테스트() throws Exception {
		//생성
		mockMvc.perform(post("/institutes")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"instituteName\":\"" + TEST_INSTITUTE_NAME +  "\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("instituteCode").exists())
				.andExpect(jsonPath("instituteName").value(TEST_INSTITUTE_NAME))
				.andDo(document("institute" + index++));

		ResultActions resultActions = mockMvc.perform(get("/institutes"))
				.andDo(document("institute" + index++));
		MvcResult mvcResult = resultActions.andReturn();

		resultActions.andExpect(status().isOk());

		List<Institute> instituteList = objectMapper
					.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Institute>>(){});
		Institute targetInstitute =
				instituteList.stream().filter(e -> e.getInstituteName().equals(TEST_INSTITUTE_NAME)).findFirst().orElse(null);

		Assert.assertEquals(TEST_INSTITUTE_NAME, targetInstitute.getInstituteName());

		//instituteName 업데이트 및 업데이트 확인
		mockMvc.perform(put("/institutes/" + targetInstitute.getInstituteCode())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"instituteCode\":\""  + targetInstitute.getInstituteCode() + "\",\"instituteName\":\"" + TEST_INSTITUTE_NAME + 2 + "\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("instituteCode").exists())
				.andExpect(jsonPath("instituteName").value(TEST_INSTITUTE_NAME + 2))
				.andDo(document("institute" + index++));


		//삭제 시에 update 확인(instituteName : "테스트은행")
		mockMvc.perform(delete("/institutes/" + targetInstitute.getInstituteCode())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("instituteCode").exists())
				.andExpect(jsonPath("instituteName").value(TEST_INSTITUTE_NAME + 2))
				.andDo(document("institute" + index++));
	}

	@Test
	public void 기관_등록_실패_케이스_기관이름중복_400_ERROR() throws Exception {
		mockMvc.perform(post("/institutes")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(
				"{\"instituteName\":\"" + DUPLICATED_INSTITUTE_NAME + "\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("code").value(HttpStatus.BAD_REQUEST.value()))
				.andDo(document("institute" + index++));/*
						responseFields(fieldWithPath("code")
								.description("Http Response 코드")),
						responseFields(fieldWithPath("message")
								.description("에러 메시지"))));*/
	}

	@Test
	public void 존재하지기관코드로_삭제를_시도하는경우_404_ERROR() throws Exception {
		mockMvc.perform(delete("/institutes/" + NOT_EXIST_INSTITUTE_CODE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("code").value(HttpStatus.NOT_FOUND.value()))
				.andDo(document("institute" + index++));
	}

	@Test
	public void 존재하지기관코드로_읽기를_시도하는경우_404_ERROR() throws Exception {
		mockMvc.perform(get("/institutes/" + NOT_EXIST_INSTITUTE_CODE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("code").value(HttpStatus.NOT_FOUND.value()))
				.andDo(document("institute" + index++));
	}

	@Test
	public void 기관목록_가져오기_태스트() throws Exception {
		mockMvc.perform(get("/institutes")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("institute" + index++));
	}

}
