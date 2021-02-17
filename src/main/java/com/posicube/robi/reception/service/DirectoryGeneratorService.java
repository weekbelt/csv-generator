package com.posicube.robi.reception.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.DepartmentAllUserData;
import com.posicube.robi.reception.domain.br.allUserData.AllUserData;
import com.posicube.robi.reception.domain.br.department.Department;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.domain.br.phoneBook.PhoneBook;
import com.posicube.robi.reception.util.CsvReaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    private final ObjectMapper objectMapper;
    private final DepartmentBRRepository departmentBRRepository;
    private final CsvReaderUtil csvReaderUtil;

    public void initCorrectedCsv() throws CsvValidationException, JsonProcessingException {
        Department.init(departmentBRRepository);
        AllUserData.init(departmentBRRepository);
        PhoneBook.init(departmentBRRepository);

        DepartmentAllUserData.initDepartmentAllUserData(departmentBRRepository);

//        DepartmentAllUserData.initDepartmentAllUserData();
//        PhoneBookDepartmentAllUserData.initDepartmentAllUserData();

        // detail한 department 삽입하기

//        // department를 제외한 Staffer csv 파일 생성
//        Set<PhoneBook> phoneBookSet = BRRepository.phoneBookSet;
//        Set<AllUserData> allUserDataSet = BRRepository.allUserDataSet;
//        BrStaffer.initPhoneBookAllUserDataExceptDepartment(phoneBookSet, allUserDataSet);

        // department 완성

    }

}
