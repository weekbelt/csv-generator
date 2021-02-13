package com.posicube.robi.reception.util.br;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public static List<Department> init() throws CsvValidationException {
        ClassPathResource departmentResource = new ClassPathResource("csv/br/rowData/department.csv");

        List<Department> series = new ArrayList<>();

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
                String departmentName = nextLine[3];

                // department를 리스트에 저장
                Department department = Department.builder()
                    .departmentCode(departmentCode)
                    .parentCode(parentCode)
                    .departmentName(departmentName)
                    .build();
                series.add(department);

                // csv 파일에 row 단위로 삽입
                String[] row = {departmentCode, parentCode, departmentName};
                correctedDepartmentCsvWriter.writeNext(row);
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }

        return series;
    }
}
