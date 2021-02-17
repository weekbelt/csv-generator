package com.posicube.robi.reception.domain.br.department;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.BRRepository;
import com.posicube.robi.reception.domain.br.department.DepartmentJson.Hierarchy;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;

@Builder
@Getter
@Setter
public class Department {

    private String departmentId;

    private String departmentCode;

    private String parentCode;

    private String departmentName;

    public static void init(DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
        createCorrectedDepartmentCsv(departmentBRRepository);
        createDepartmentHierarchy(departmentBRRepository);
    }

    private static void createCorrectedDepartmentCsv(DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
        ClassPathResource departmentResource = new ClassPathResource("csv/br/rowData/department.csv");

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedDepartment.csv";

        try (
            CSVReader csvReader = new CSVReader(new FileReader(departmentResource.getFile()));
            CSVWriter correctedDepartmentCsvWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            // column 명 제외
            csvReader.readNext();
            // column 명 포함
            correctedDepartmentCsvWriter.writeNext(new String[]{"departmentId", "departmentCode", "parentCode", "departmentName"});

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String departmentId = nextLine[4];
                String departmentCode = nextLine[1];
                String parentCode = nextLine[2];
                String departmentName = DepartmentFilter.correctedDepartmentName(nextLine[3]);

                // DepartmentBR를 DB에 저장
                DepartmentBR departmentBR = DepartmentBR.builder()
                    .departmentId(Long.parseLong(departmentId))
                    .departmentCode(departmentCode)
                    .parentCode(parentCode)
                    .departmentName(departmentName)
                    .build();
                departmentBRRepository.save(departmentBR);

                // csv 파일에 row 단위로 삽입
                String[] row = {departmentId, departmentCode, parentCode, departmentName};
                correctedDepartmentCsvWriter.writeNext(row);
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }
    }

    public static void createDepartmentHierarchy(DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
        Map<Long, List<Hierarchy>> hierarchyMap = BRRepository.hierarchyMap;
        List<DepartmentBR> departmentBRList = departmentBRRepository.findAll();
        for (DepartmentBR departmentBR : departmentBRList) {
            Long departmentId = departmentBR.getDepartmentId();
            String parentCode = departmentBR.getParentCode();

            List<Hierarchy> hierarchyList = new ArrayList<>();
            while (!parentCode.equals("-1")) {
                String finalParentCode = parentCode;
                DepartmentBR parentDepartmentBR = departmentBRRepository.findDepartmentBRByDepartmentCode(parentCode)
                    .orElseThrow(() -> new IllegalArgumentException("부서코드에 해당하는 부서가 존재하지 않습니다. departmentCode: " + finalParentCode));

                Hierarchy hierarchy = Hierarchy.builder()
                    .id(String.valueOf(parentDepartmentBR.getDepartmentId()))
                    .name(parentDepartmentBR.getDepartmentName())
                    .build();
                hierarchyList.add(hierarchy);

                parentCode = parentDepartmentBR.getParentCode();
            }

            Collections.reverse(hierarchyList);
            hierarchyMap.put(departmentId, hierarchyList);
        }
    }
}
