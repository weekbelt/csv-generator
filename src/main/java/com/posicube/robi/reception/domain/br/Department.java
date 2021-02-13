package com.posicube.robi.reception.domain.br;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;

@Builder
@Getter
@Setter
public class Department {

    private String departmentCode;

    private String parentCode;

    private String departmentName;

    public static void init() throws CsvValidationException {
        ClassPathResource departmentResource = new ClassPathResource("csv/br/rowData/department.csv");

//        List<Department> series = new ArrayList<>();
        Set<Department> departments = BRRepository.departmentSet;

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedDepartment.csv";

        try (
            CSVReader csvReader = new CSVReader(new FileReader(departmentResource.getFile()));
            CSVWriter correctedDepartmentCsvWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            // column 명 제외
            csvReader.readNext();
            // column 명 포함
            correctedDepartmentCsvWriter.writeNext(new String[]{"departmentCode", "parentCode", "departmentName"});

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String departmentCode = nextLine[1];
                String parentCode = nextLine[2];
                String departmentName = correctedDepartmentName(nextLine[3]);

                // department를 리스트에 저장
                Department department = Department.builder()
                    .departmentCode(departmentCode)
                    .parentCode(parentCode)
                    .departmentName(departmentName)
                    .build();
                departments.add(department);
//                series.add(department);

                // csv 파일에 row 단위로 삽입
                String[] row = {departmentCode, parentCode, departmentName};
                correctedDepartmentCsvWriter.writeNext(row);
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }
    }

    public static String correctedDepartmentName(String departmentName) {
        if (departmentName.endsWith("읍") || departmentName.endsWith("면")) {
            departmentName += "사무소";
        } else if (departmentName.endsWith("동")) {
            departmentName += "주민센터";
        } else if (departmentName.endsWith("부시장") || departmentName.endsWith("시장")) {
            departmentName += "실";
        }
        return departmentName;
    }
}
