package com.tmoncorp;

import com.tmoncorp.institute.domain.Institute;
import com.tmoncorp.institute.service.InstituteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@SpringBootApplication
public class SupportAmountApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportAmountApplication.class, args);
	}

	@Bean
	CommandLineRunner init(InstituteService instituteService) {
		return args -> {
			List<String> instituteList =
					Arrays.asList(new String[]{"주택도시기금", "국민은행", "우리은행", "신한은행", "한국시티은행",
							"하나은행", "농협은행/수협은행", "외환은행", "기타은행"});
			List<Institute> institutes = instituteList.stream()
					.map(e -> new Institute(e))
					.filter(e -> false == instituteService.isExistInstituteName(e.getInstituteName()))
						.collect(Collectors.toList());

			List<Institute> returnInstituteList = new ArrayList<>();

			for (Institute institute : institutes) {
				Institute returnInstitute = instituteService.createInstitute(institute);
				returnInstituteList.add(returnInstitute);
			}

			for (Institute institute : returnInstituteList) {
				log.debug("institute : {}", institute);
			}
		};

	}
}
