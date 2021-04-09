package com.posicube.robi.reception.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.department.DepartmentJson;
import com.posicube.robi.reception.domain.br.department.DepartmentJson.Hierarchy;
import com.posicube.robi.reception.domain.br.department.DepartmentJson.ParentDept;
import com.posicube.robi.reception.domain.br.department.DepartmentJson.Phone;
import com.posicube.robi.reception.domain.dongnae.DepartmentSeries;
import com.posicube.robi.reception.domain.dongnae.DepartmentSeriesRepository;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    private final ObjectMapper objectMapper;
    private final CsvReaderUtil csvReaderUtil;
    private final DepartmentSeriesRepository departmentSeriesRepository;

    public List<DepartmentJson> generateDirectoryDepartment(String branchId) throws CsvValidationException {
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
                ParentDept parentDept = getParentDept(departmentSeries);
                List<Hierarchy> hierarchyList = getHierarchyList(departmentSeries);

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
        return departmentJsonList;
    }

    private List<Hierarchy> getHierarchyList(DepartmentSeries departmentSeries) {

        List<Hierarchy> hierarchyList = new ArrayList<>();
        DepartmentSeries currentDepartmentSeries = departmentSeries;
        while (true) {
            String parentDepartmentCode = currentDepartmentSeries.getParentDepartmentCode();
            Optional<DepartmentSeries> optionalDepartmentSeries = departmentSeriesRepository.findByDepartmentCode(parentDepartmentCode);
            if (optionalDepartmentSeries.isPresent()) {
                DepartmentSeries parentDepartmentSeries = optionalDepartmentSeries.get();
                Hierarchy hierarchy = Hierarchy.builder()
                    .id(String.valueOf(parentDepartmentSeries.getId()))
                    .name(parentDepartmentSeries.getDepartmentName())
                    .build();
                hierarchyList.add(hierarchy);
                currentDepartmentSeries = parentDepartmentSeries;
            } else {
                break;
            }
        }
        return hierarchyList.stream().sorted(Comparator.comparing(Hierarchy::getId)).collect(Collectors.toList());
    }

    private ParentDept getParentDept(DepartmentSeries departmentSeries) {
        String parentDepartmentCode = departmentSeries.getParentDepartmentCode();
        DepartmentSeries parentDepartmentSeries = departmentSeriesRepository.findByDepartmentCode(parentDepartmentCode)
            .orElseThrow(() -> new EntityNotFoundException("해당 DepartmentSeries를 찾을 수 없습니다. departmentCode: " + parentDepartmentCode));

        Phone phone = getPhone(parentDepartmentSeries.getDepartmentPhoneNumber());
        return ParentDept.builder()
            .id(parentDepartmentSeries.getId())
            .name(parentDepartmentSeries.getDepartmentName())
            .phone(phone)
            .build();
    }

    private Phone getPhone(String number) {
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

    public void generateDirectoryStaffer() {

    }
}
