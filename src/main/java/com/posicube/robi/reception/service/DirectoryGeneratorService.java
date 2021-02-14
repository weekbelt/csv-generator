package com.posicube.robi.reception.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.AllUserData;
import com.posicube.robi.reception.domain.br.Department;
import com.posicube.robi.reception.domain.br.DepartmentAllUserData;
import com.posicube.robi.reception.domain.br.PhoneBook;
import com.posicube.robi.reception.domain.br.PhoneBookDepartmentAllUserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    private final ObjectMapper objectMapper;

    public void initCorrectedCsv() throws CsvValidationException, JsonProcessingException {
        Department.init();
        AllUserData.init();
        PhoneBook.init();

        DepartmentAllUserData.initDepartmentAllUserData();
        PhoneBookDepartmentAllUserData.initDepartmentAllUserData();

        // detail한 department 삽입하기


//        // department를 제외한 Staffer csv 파일 생성
//        Set<PhoneBook> phoneBookSet = BRRepository.phoneBookSet;
//        Set<AllUserData> allUserDataSet = BRRepository.allUserDataSet;
//        BrStaffer.initPhoneBookAllUserDataExceptDepartment(phoneBookSet, allUserDataSet);

        // department 완성

    }

}
