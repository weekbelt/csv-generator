package com.posicube.robi.reception.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.DepartmentAllUserData;
import com.posicube.robi.reception.domain.br.allUserData.AllUserData;
import com.posicube.robi.reception.domain.br.department.Department;
import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.domain.br.department.DepartmentJson;
import com.posicube.robi.reception.domain.br.phoneBook.PhoneBook;
import com.posicube.robi.reception.domain.br.staffer.BrStaffer;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    private final ObjectMapper objectMapper;
    private final DepartmentBRRepository departmentBRRepository;
    private final CsvReaderUtil csvReaderUtil;

    public void initCorrectedCsv() throws CsvValidationException, JsonProcessingException, IOException {
        Department.init(departmentBRRepository);
        AllUserData.init(departmentBRRepository);
        PhoneBook.init(departmentBRRepository);

        DepartmentAllUserData.initDepartmentAllUserData(departmentBRRepository);
        BrStaffer.initPhoneBookAllUserDataExceptDepartment(csvReaderUtil, departmentBRRepository);

//        // departmentBR에 전화번호 붙이기
//        ClassPathResource pastDepartmentResource = new ClassPathResource("csv/br/boryeong_department_dump_2020_07_08_181417.csv");
//        List<String[]> pastDepartmentList = csvReaderUtil.convertCsvResourceToDataFrame(pastDepartmentResource);
//
//        List<DepartmentBR> departmentBRList = departmentBRRepository.findAll();
//        for (DepartmentBR departmentBR : departmentBRList) {
//            for (String[] pastDepartment : pastDepartmentList) {
//                String name = pastDepartment[1];
//                String phoneNumber =
//            }
//        }
//        ;

        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<DepartmentJson> pastDepartmentJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/br/past/department.json"), new TypeReference<List<DepartmentJson>>() {
        });

//        List<DepartmentBR> departmentBRList = departmentBRRepository.findAll();
//        for (DepartmentBR departmentBR : departmentBRList) {
//            DepartmentBR parentDepartmentBR = departmentBRRepository.findDepartmentBRByDepartmentCode(departmentBR.getParentCode())
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 부서가 존재하지 않습니다. departmentCode: " + departmentBR.getParentCode()));
//            for (DepartmentJson pastDepartmentJson : pastDepartmentJsonList) {
//                // 같은 부서라면
//                if (isSameDepartment(departmentBR, parentDepartmentBR, pastDepartmentJson)) {
//
//                }
//            }
//        }

    }

    private boolean isSameDepartment(DepartmentBR departmentBR, DepartmentBR parentDepartmentBR, DepartmentJson pastDepartmentJson) {
        return departmentBR.getDepartmentName().equals(pastDepartmentJson.getDeptName()) && parentDepartmentBR.getDepartmentName().equals(pastDepartmentJson.getParentDept().getName());
    }

}
