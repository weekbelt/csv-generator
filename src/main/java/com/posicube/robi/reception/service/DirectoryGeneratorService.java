package com.posicube.robi.reception.service;

import antlr.preprocessor.Hierarchy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.department.DepartmentJson;
import com.posicube.robi.reception.domain.department.DepartmentJson.ParentDept;
import com.posicube.robi.reception.domain.department.DepartmentJson.Phone;
import com.posicube.robi.reception.domain.department.DepartmentSeries;
import com.posicube.robi.reception.domain.department.DepartmentSeriesRepository;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.domain.staffer.StafferJson.Department;
import com.posicube.robi.reception.domain.staffer.StafferSeries;
import com.posicube.robi.reception.domain.staffer.StafferSeriesRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    private final ObjectMapper objectMapper;
    private final CsvReaderUtil csvReaderUtil;
    private final DepartmentSeriesRepository departmentSeriesRepository;
    private final StafferSeriesRepository stafferSeriesRepository;

    public Resource generateDirectoryDepartment(String branchId) throws CsvValidationException, IOException {
        ClassPathResource departmentCsv = new ClassPathResource("csv/dongnae/department.csv");
        List<String[]> departmentDF = csvReaderUtil.convertCsvResourceToDataFrame(departmentCsv);

        saveDepartmentDataFrame(departmentDF);

        List<DepartmentJson> departmentJsonList = new ArrayList<>();
        List<DepartmentSeries> departmentSeriesList = departmentSeriesRepository.findAll();
        for (DepartmentSeries departmentSeries : departmentSeriesList) {
            if (StringUtils.isBlank(departmentSeries.getParentDepartmentCode())) {
                DepartmentJson departmentJson = DepartmentJson.builder()
                    .deptName(departmentSeries.getDepartmentName())
                    .departmentID(departmentSeries.getId())
                    .bchID(branchId)
                    .phone(objectMapper.valueToTree(departmentSeries.getDepartmentPhoneNumber()))
                    .build();
                departmentJsonList.add(departmentJson);
            } else {
                // parentDepth 생성
                ParentDept parentDept = getParentDept(departmentSeries);

                // hierarchy 생성
                List<DepartmentJson.Hierarchy> hierarchyList = getHierarchyList(departmentSeries);

                DepartmentJson departmentJson = DepartmentJson.builder()
                    .depth(hierarchyList.size())
                    .hierarchy(hierarchyList)
                    .deptName(departmentSeries.getDepartmentName())
                    .departmentID(departmentSeries.getId())
                    .bchID(branchId)
                    .phone(objectMapper.valueToTree(departmentSeries.getDepartmentPhoneNumber()))
                    .parentDept(parentDept)
                    .build();
                departmentJsonList.add(departmentJson);
            }
        }

        String filePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/csv-generator/src/main/resources/json/dongnae";
        String saveFileName = File.separator + "department.json";

        File departmentJsonFile = JsonUtil.createDepartmentJsonFile(filePath, saveFileName, departmentJsonList, objectMapper);
        return new FileSystemResource(departmentJsonFile);
    }

    private List<DepartmentJson.Hierarchy> getHierarchyList(DepartmentSeries departmentSeries) {

        List<DepartmentJson.Hierarchy> hierarchyList = new ArrayList<>();
        DepartmentSeries currentDepartmentSeries = departmentSeries;
        while (true) {
            String parentDepartmentCode = currentDepartmentSeries.getParentDepartmentCode();
            Optional<DepartmentSeries> optionalDepartmentSeries = departmentSeriesRepository.findByDepartmentCode(parentDepartmentCode);
            if (optionalDepartmentSeries.isPresent()) {
                DepartmentSeries parentDepartmentSeries = optionalDepartmentSeries.get();
                DepartmentJson.Hierarchy hierarchy = DepartmentJson.Hierarchy.builder()
                    .id(String.valueOf(parentDepartmentSeries.getId()))
                    .name(parentDepartmentSeries.getDepartmentName())
                    .build();
                hierarchyList.add(hierarchy);
                currentDepartmentSeries = parentDepartmentSeries;
            } else {
                break;
            }
        }
        return hierarchyList.stream().sorted(Comparator.comparing(DepartmentJson.Hierarchy::getId)).collect(Collectors.toList());
    }

    private ParentDept getParentDept(DepartmentSeries departmentSeries) {
        String parentDepartmentCode = departmentSeries.getParentDepartmentCode();
        DepartmentSeries parentDepartmentSeries = departmentSeriesRepository.findByDepartmentCode(parentDepartmentCode)
            .orElseThrow(() -> new EntityNotFoundException("해당 DepartmentSeries를 찾을 수 없습니다. departmentCode: " + parentDepartmentCode));

        Phone phone = getDepartmentPhone(parentDepartmentSeries.getDepartmentPhoneNumber());
        return ParentDept.builder()
            .id(parentDepartmentSeries.getId())
            .name(parentDepartmentSeries.getDepartmentName())
            .phone(phone)
            .build();
    }

    private Phone getDepartmentPhone(String number) {
        return Phone.builder()
            .number(number)
            .type("내선")
            .build();
    }

    private void saveDepartmentDataFrame(List<String[]> departmentDF) {
        for (String[] series : departmentDF) {
            DepartmentSeries departmentSeries = DepartmentSeries.builder()
                .departmentCode(series[0])
                .departmentName(series[1])
                .parentDepartmentCode(series[2])
                .departmentPhoneNumber(series[3])
                .build();
            departmentSeriesRepository.save(departmentSeries);
        }
    }

    public Resource generateDirectoryStaffer(String branchId) throws CsvValidationException, IOException {
        ClassPathResource stafferCsv = new ClassPathResource("csv/dongnae/staffer.csv");
        List<String[]> stafferDF = csvReaderUtil.convertCsvResourceToDataFrame(stafferCsv);

        saveStafferDataFrame(stafferDF);

        List<StafferJson> stafferJsonList = new ArrayList<>();
        List<StafferSeries> stafferSeriesList = stafferSeriesRepository.findAll();
        for (StafferSeries stafferSeries : stafferSeriesList) {
            // phone 생성
            StafferJson.Phone phone = getStafferPhone(stafferSeries.getPhoneNumber());

            // phone2 생성
            DepartmentSeries departmentSeries = departmentSeriesRepository.findByDepartmentCode(stafferSeries.getDepartmentCode())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 department를 찾을 수 없습니다. departmentCode: " + stafferSeries.getDepartmentCode()));
            JsonNode phone2 = getPhone2(departmentSeries);

            // departmentId 생성
            Long departmentId = departmentSeries.getId();

            // jobs 생성
            List<String> jobs = Arrays.stream(stafferSeries.getJobs().split(","))
                .map(String::trim).collect(Collectors.toList());

            // positions 생성
            List<String> positions = Arrays.stream(stafferSeries.getJobs().split(","))
                .map(String::trim).collect(Collectors.toList());

            // departmentHierarchy 생성
            List<Department> hierarchyList = getStafferHierarchyList(departmentSeries);

            StafferJson stafferJson = StafferJson.builder()
                .staffID(stafferSeries.getId())
                .firstName(stafferSeries.getFirstName())
                .lastName(stafferSeries.getLastName())
                .fullName(stafferSeries.getFullName())
                .phone(phone)
                .phone2(phone2)
                .jobs(jobs)
                .departmentID(departmentId)
                .entityDisplayName("staffer")
                .departmentHierarchy(hierarchyList)
                .department(departmentSeries.getDepartmentName())
                .positions(positions)
                .bchID(branchId)
                .isAdmin(false)
                .build();
            stafferJsonList.add(stafferJson);
        }

        String filePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/csv-generator/src/main/resources/json/dongnae";
        String saveFileName = File.separator + "staffer.json";

        File stafferJsonFile = JsonUtil.createStafferJsonFile(filePath, saveFileName, stafferJsonList, objectMapper);
        return new FileSystemResource(stafferJsonFile);
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

    private void saveStafferDataFrame(List<String[]> stafferDF) {
        for (String[] series : stafferDF) {
            StafferSeries stafferSeries = StafferSeries.builder()
                .departmentCode(series[0])
                .departmentName(series[1])
                .positionName(series[2])
                .fullName(series[3])
                .phoneNumber(series[4])
                .jobs(series[5])
                .areas(series[6])
                .build();
            stafferSeriesRepository.save(stafferSeries);
        }
    }
}
