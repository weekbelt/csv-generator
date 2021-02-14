package com.posicube.robi.reception.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.AllUserData;
import com.posicube.robi.reception.domain.br.BRRepository;
import com.posicube.robi.reception.domain.br.Department;
import com.posicube.robi.reception.domain.br.PhoneBook;
import java.util.Set;
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

//        Set<Department> departmentSet = BRRepository.departmentMap;
//        Set<AllUserData> allUserDataSet = BRRepository.allUserDataSet;
//        Set<PhoneBook> phoneBookSet = BRRepository.phoneBookSet;
//
//
    }

}
