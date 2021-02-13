package com.posicube.robi.reception.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.util.br.AllUserData;
import com.posicube.robi.reception.util.br.Department;
import com.posicube.robi.reception.util.br.PhoneBook;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    private final ObjectMapper objectMapper;

    public void initCorrectedCsv() throws CsvValidationException, JsonProcessingException {
        List<AllUserData> allUserData = AllUserData.init();
        System.out.println(objectMapper.writeValueAsString(allUserData));
        List<Department> departmentList = Department.init();
        System.out.println(objectMapper.writeValueAsString(departmentList));
        List<PhoneBook> phoneBookList = PhoneBook.init();
        System.out.println(objectMapper.writeValueAsString(phoneBookList));
    }
}
