package com.posicube.robi.reception.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.BRRepository;
import com.posicube.robi.reception.domain.br.DepartmentAllUserData;
import com.posicube.robi.reception.domain.br.allUserData.AllUserData;
import com.posicube.robi.reception.domain.br.department.Department;
import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.domain.br.department.DepartmentJson.Hierarchy;
import com.posicube.robi.reception.domain.br.phoneBook.PhoneBook;
import com.posicube.robi.reception.domain.br.staffer.BrStaffer;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        BrStaffer.initPhoneBookAllUserDataExceptDepartment(csvReaderUtil, departmentBRRepository);

        // 로그
        Map<Long, List<Hierarchy>> hierarchyMap = BRRepository.hierarchyMap;
        for (Long key : hierarchyMap.keySet()) {
            System.out.println(objectMapper.writeValueAsString(hierarchyMap.get(key)));
        }

        final List<DepartmentBR> all = departmentBRRepository.findAll();
        System.out.println(objectMapper.writeValueAsString(all));

    }

}
