package com.posicube.robi.reception.domain.staffer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.department.DepartmentSeries;
import com.posicube.robi.reception.domain.department.repository.DepartmentSeriesRepository;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.domain.staffer.StafferJson.Department;
import com.posicube.robi.reception.domain.staffer.StafferJsonFactory;
import com.posicube.robi.reception.domain.staffer.StafferSeries;
import com.posicube.robi.reception.domain.staffer.repository.StafferSeriesRepository;
import com.posicube.robi.reception.util.CsvReaderUtil;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class StafferJsonService {

    private final ObjectMapper objectMapper;
    private final CsvReaderUtil csvReaderUtil;
    private final DepartmentSeriesRepository departmentSeriesRepository;
    private final StafferSeriesRepository stafferSeriesRepository;

    public Resource generateDirectoryStaffer(String branchId) throws CsvValidationException, IOException {
        ClassPathResource stafferCsv = new ClassPathResource("csv/dongnae/staffer_ex.csv");
        List<String[]> stafferDF = csvReaderUtil.convertCsvResourceToDataFrame(stafferCsv);

        saveStafferDataFrame(stafferDF, branchId);

        List<StafferJson> stafferJsonList = getStafferJsonList();

        String filePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/csv-generator/src/main/resources/json/dongnae";
        String saveFileName = File.separator + "staffer.json";

        File stafferJsonFile = JsonUtil.createStafferJsonFile(filePath, saveFileName, stafferJsonList, objectMapper);
        return new FileSystemResource(stafferJsonFile);
    }

    private List<StafferJson> getStafferJsonList() {
        List<StafferJson> stafferJsonList = new ArrayList<>();
        List<StafferSeries> stafferSeriesList = stafferSeriesRepository.findAll();
        for (StafferSeries stafferSeries : stafferSeriesList) {
            // phone 생성
            StafferJson.Phone phone = getStafferPhone(stafferSeries.getPhoneNumber());

            // phone2 생성
            DepartmentSeries departmentSeries = departmentSeriesRepository.findByDepartmentCode(stafferSeries.getDepartmentCode())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 department를 찾을 수 없습니다. departmentCode: " + stafferSeries.getDepartmentCode()));
            JsonNode phone2 = getPhone2(departmentSeries);

            // jobs 생성
            List<String> jobs = Arrays.stream(stafferSeries.getJobs().split(","))
                .map(String::trim).collect(Collectors.toList());

            // positions 생성
            List<String> positions = Arrays.stream(stafferSeries.getJobs().split(","))
                .map(String::trim).collect(Collectors.toList());

            // departmentHierarchy 생성
            List<Department> hierarchyList = getStafferHierarchyList(departmentSeries);

            StafferJson stafferJson = StafferJsonFactory.createStafferJson(stafferSeries, phone, departmentSeries, phone2, jobs, positions, hierarchyList);
            stafferJsonList.add(stafferJson);
        }
        return stafferJsonList;
    }

    private List<Department> getStafferHierarchyList(DepartmentSeries departmentSeries) {

        List<Department> hierarchyList = new ArrayList<>();
        DepartmentSeries currentDepartmentSeries = departmentSeries;
        while (true) {
            String parentDepartmentCode = currentDepartmentSeries.getParentDepartmentCode();
            Optional<DepartmentSeries> optionalDepartmentSeries = departmentSeriesRepository.findByDepartmentCode(parentDepartmentCode);
            if (optionalDepartmentSeries.isPresent()) {
                DepartmentSeries parentDepartmentSeries = optionalDepartmentSeries.get();
                Department hierarchy = Department.builder()
                    .id(String.valueOf(parentDepartmentSeries.getId()))
                    .name(parentDepartmentSeries.getDepartmentName())
                    .build();
                hierarchyList.add(hierarchy);
                currentDepartmentSeries = parentDepartmentSeries;
            } else {
                break;
            }
        }
        return hierarchyList.stream().sorted(Comparator.comparing(Department::getId)).collect(Collectors.toList());
    }

    private StafferJson.Phone getStafferPhone(String number) {
        return StafferJson.Phone.builder()
            .number(number)
            .type("내선")
            .build();
    }

    private JsonNode getPhone2(DepartmentSeries departmentSeries) {
        StafferJson.Phone phone2 = StafferJson.Phone.builder()
            .type("내선")
            .number(departmentSeries.getDepartmentPhoneNumber())
            .build();
        return objectMapper.valueToTree(phone2);
    }

    private void saveStafferDataFrame(List<String[]> stafferDF, String branchId) {
        for (String[] series : stafferDF) {
            StafferSeries stafferSeries = StafferSeries.builder()
                .departmentCode(series[0])
                .departmentName(series[1])
                .positionName(series[2])
                .fullName(series[3])
                .phoneNumber(series[4])
                .jobs(series[5])
                .areas(series[6])
                .branchId(branchId)
                .build();
            stafferSeriesRepository.save(stafferSeries);
        }
    }
}
