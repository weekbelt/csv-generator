package com.posicube.robi.reception.domain.department.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.department.DepartmentJson;
import com.posicube.robi.reception.domain.department.DepartmentJson.ParentDept;
import com.posicube.robi.reception.domain.department.DepartmentJson.Phone;
import com.posicube.robi.reception.domain.department.DepartmentJsonFactory;
import com.posicube.robi.reception.domain.department.DepartmentSeries;
import com.posicube.robi.reception.domain.department.repository.DepartmentSeriesRepository;
import com.posicube.robi.reception.util.CsvReaderUtil;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class DepartmentJsonService {

    private final ObjectMapper objectMapper;
    private final CsvReaderUtil csvReaderUtil;
    private final DepartmentSeriesRepository departmentSeriesRepository;

    public Resource generateDirectoryDepartment(String branchId) throws CsvValidationException, IOException {
        List<String[]> departmentDF = csvReaderUtil.convertCsvResourceToDataFrame(new ClassPathResource("csv/gbdc/department.csv"));

        saveDepartmentDataFrame(departmentDF, branchId);
        List<DepartmentJson> departmentJsonList = getDepartmentJsonList();

        String filePath = "/Users/joohyuk/Documents/GitHub/csv-generator/src/main/resources/json/gbdc";
        String saveFileName = File.separator + "department.json";
        File departmentJsonFile = JsonUtil.createDepartmentJsonFile(filePath, saveFileName, departmentJsonList, objectMapper);
        return new FileSystemResource(departmentJsonFile);
    }

    private List<DepartmentJson> getDepartmentJsonList() {
        List<DepartmentJson> departmentJsonList = new ArrayList<>();
        List<DepartmentSeries> departmentSeriesList = departmentSeriesRepository.findAll();
        for (DepartmentSeries departmentSeries : departmentSeriesList) {
            List<DepartmentJson.Hierarchy> hierarchyList = getHierarchyList(departmentSeries);
            JsonNode phone = getPhone(departmentSeries);
            ParentDept parentDept = getParentDept(departmentSeries);

            DepartmentJson departmentJson = DepartmentJsonFactory.createDepartmentJson(hierarchyList, departmentSeries, phone, parentDept);
            departmentJsonList.add(departmentJson);
        }
        return departmentJsonList;
    }

    private List<DepartmentJson.Hierarchy> getHierarchyList(DepartmentSeries departmentSeries) {
        List<DepartmentJson.Hierarchy> hierarchyList = new ArrayList<>();
        DepartmentSeries currentDepartmentSeries = departmentSeries;
        if (currentDepartmentSeries.getParentDepartmentCode().isBlank()) {
            return new ArrayList<>();
        }
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

    private JsonNode getPhone(DepartmentSeries departmentSeries) {
        DepartmentJson.Phone phone;
        if (departmentSeries.getDepartmentPhoneNumber().length() == 12 || departmentSeries.getDepartmentPhoneNumber().length() == 13) {
            phone = DepartmentJson.Phone.builder()
                .type("외부국선")
                .number(departmentSeries.getDepartmentPhoneNumber())
                .build();
        } else {
            phone = DepartmentJson.Phone.builder()
                .type("내선")
                .number(departmentSeries.getDepartmentPhoneNumber())
                .build();
        }
        return objectMapper.valueToTree(phone);
    }

    private ParentDept getParentDept(DepartmentSeries departmentSeries) {
        String parentDepartmentCode = departmentSeries.getParentDepartmentCode();
        if (parentDepartmentCode.isBlank()) {
            return null;
        }
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
        if (number.length() == 12 || number.length() == 13) {
            return Phone.builder()
                .number(number)
                .type("외부국선")
                .build();
        } else {
            return Phone.builder()
                .number(number)
                .type("내선")
                .build();

        }
    }

    private void saveDepartmentDataFrame(List<String[]> departmentDF, String branchId) {
        for (String[] series : departmentDF) {
            DepartmentSeries departmentSeries = DepartmentSeries.builder()
                .departmentCode(series[0])
                .departmentName(series[1])
                .parentDepartmentCode(series[2])
                .departmentPhoneNumber(series[3])
                .branchId(branchId)
                .build();
            departmentSeriesRepository.save(departmentSeries);
        }
    }
}
