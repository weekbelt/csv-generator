package com.posicube.robi.reception.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.dongnae.DepartmentDataFrame;
import com.posicube.robi.reception.domain.dongnae.DepartmentDataFrameRepository;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    private final ObjectMapper objectMapper;
    private final CsvReaderUtil csvReaderUtil;
    private final DepartmentDataFrameRepository departmentDataFrameRepository;

    public void generateDirectoryDepartment() throws CsvValidationException {
        ClassPathResource departmentCsv = new ClassPathResource("csv/dongnae/department.csv");
        List<String[]> departmentDF = csvReaderUtil.convertCsvResourceToDataFrame(departmentCsv);

        saveDepartmentDataFrame(departmentDF);
    }

    private void saveDepartmentDataFrame(List<String[]> departmentDF) {
        for (String[] series : departmentDF) {
            DepartmentDataFrame departmentDataFrame = DepartmentDataFrame.builder()
                .departmentCode(series[0])
                .departmentName(series[1])
                .parentDepartmentCode(series[2])
                .departmentPhoneNumber(series[3])
                .build();
            departmentDataFrameRepository.save(departmentDataFrame);
        }
    }

    public void generateDirectoryStaffer() {

    }
}
