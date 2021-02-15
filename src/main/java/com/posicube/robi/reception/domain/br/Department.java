package com.posicube.robi.reception.domain.br;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
        ClassPathResource departmentResource = new ClassPathResource("csv/br/rowData/department.csv");

        Map<String, Department> departmentMap = BRRepository.departmentMap;

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
                String departmentName = correctedDepartmentName(nextLine[3]);

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

    public static void addDepartment() throws CsvValidationException {
        ClassPathResource phonebookDepartmentAllUserDataResource = new ClassPathResource("csv/br/correctedData/phoneBookDepartmentAllUserData.csv");

        CsvReaderUtil csvReaderUtil = new CsvReaderUtil();
        List<String[]> phoneBookDepartmentAllUserDataList = csvReaderUtil.convertCsvResourceToDataFrame(phonebookDepartmentAllUserDataResource);
        Map<String, Department> departmentMap = BRRepository.departmentMap;

        int endPointNumber = 1;
        for (String[] phoneBookDepartmentAllUserData : phoneBookDepartmentAllUserDataList) {
            String departmentName = phoneBookDepartmentAllUserData[3];
            String departmentCode = phoneBookDepartmentAllUserData[1];
            String detailDepartmentName = phoneBookDepartmentAllUserData[12];
            if (!departmentName.equals(detailDepartmentName) && StringUtils.isNotBlank(detailDepartmentName)) {
                departmentCode = departmentCode + "." + endPointNumber;
                endPointNumber++;
            }
        }

    }
}
