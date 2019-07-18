package com.tmoncorp.institute.service;

import com.tmoncorp.common.service.SequenceGeneratorService;
import com.tmoncorp.institute.domain.Institute;
import com.tmoncorp.institute.exception.InstituteDuplicationException;
import com.tmoncorp.institute.exception.InstituteNotFoundException;
import com.tmoncorp.institute.repository.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstituteService {
    @Autowired
    private InstituteRepository instituteRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    public Institute deleteInstitute(final String instituteCode) throws InstituteNotFoundException {
        Optional<Institute> optionalInstitute = instituteRepository.findById(instituteCode);

        optionalInstitute.orElseThrow(() -> new InstituteNotFoundException(instituteCode));

        optionalInstitute.ifPresent((institute) -> instituteRepository.deleteById(institute.getInstituteCode()));

        return optionalInstitute.get();
    }

    public Institute createInstitute(final Institute institute) throws InstituteDuplicationException {

        Optional<Institute> optionalInstitute = instituteRepository.findFirstByInstituteName(institute.getInstituteName());

        if (optionalInstitute.isPresent()) {
            throw new InstituteDuplicationException("InstituteName : " + institute.getInstituteName() + "는 이미 존재합니다.");
        }

        institute.setInstituteCode(sequenceGeneratorService.generateInstituteSequence(Institute.SEQUENCE_NAME));

        return instituteRepository.insert(institute);
    }

    public Institute getInstituteByInstituteCode(final String instituteCode) throws InstituteNotFoundException {
        Optional<Institute> optionalInstitute = instituteRepository.findById(instituteCode);

        if (false == optionalInstitute.isPresent()) {
            throw new InstituteNotFoundException(instituteCode);
        }

        return optionalInstitute.get();
    }

    public boolean isExistInstituteName(final String instituteName) {
        Optional<Institute> optionalInstitute = instituteRepository.findFirstByInstituteName(instituteName);

        return optionalInstitute.isPresent();
    }


    public Institute updateInstitute(final Institute institute) throws InstituteDuplicationException {

        Optional<Institute> optionalInstitute = instituteRepository.findFirstByInstituteName(institute.getInstituteName());

        if (optionalInstitute.isPresent()) {
            throw new InstituteDuplicationException("InstituteName : " + institute.getInstituteName() + "은 이미 존재합니다.");
        }

        return instituteRepository.save(institute);
    }

    public List<Institute> getAllInstitute() {
        return  instituteRepository.findAll();
    }
}
