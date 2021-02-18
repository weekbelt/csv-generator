package com.posicube.robi.reception.domain.br.department;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

public class BrDepartment {

    private static void createFinalDepartmentCsv(DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
//        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/finalData/finalDepartment.csv";
//
//        try (
//            CSVWriter finalDepartmentCsv = new CSVWriter(new FileWriter(csvFilePath))
//        ) {
//            // column 명 포함
//            finalDepartmentCsv.writeNext(new String[]{"departmentId", "departmentName", "parentDepartmentId", "parentDepartmentName", "phoneType", "phoneNumber"});
//
//            String[] nextLine;
//            while ((nextLine = csvReader.readNext()) != null) {
//                String departmentId = nextLine[4];
//                String departmentCode = nextLine[1];
//                String parentCode = nextLine[2];
//                String departmentName = DepartmentFilter.correctedDepartmentName(nextLine[3]);
//
//                // DepartmentBR를 DB에 저장
//                DepartmentBR departmentBR = DepartmentBR.builder()
//                    .departmentId(Long.parseLong(departmentId))
//                    .departmentCode(departmentCode)
//                    .parentCode(parentCode)
//                    .departmentName(departmentName)
//                    .build();
//                departmentBRRepository.save(departmentBR);
//
//                // csv 파일에 row 단위로 삽입
//                String[] row = {departmentId, departmentCode, parentCode, departmentName};
//                finalDepartmentCsv.writeNext(row);
//            }
//        } catch (IOException e) {
//            throw new CsvFileHandlingException("Csv file reading failed!!", e);
//        }
    }
}
