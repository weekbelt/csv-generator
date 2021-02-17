package com.posicube.robi.reception.domain.br.allUserData;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.BRRepository;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

@Builder
@Getter
@Setter
public class AllUserData {

    private String stafferId;

    private String departmentCode;

    private String departmentName;

    private String stafferName;

    private String position;

    private String phoneType;

    private String phoneNumber;

    private String jobs;

    public static void init(DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
        ClassPathResource allUserDataResource = new ClassPathResource("csv/br/rowData/allUserData.csv");

        Set<AllUserData> allUserDataSet = BRRepository.allUserDataSet;

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedAllUserData.csv";
        try (
            CSVReader csvReader = new CSVReader(new FileReader(allUserDataResource.getFile()));
            CSVWriter correctedAllUserCsvWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            // column 명 제외
            csvReader.readNext();
            // column 명 포함
            correctedAllUserCsvWriter.writeNext(
                new String[]{"stafferId", "departmentCode", "departmentName", "stafferName", "position", "phoneType",
                    "phoneNumber", "jobs"});

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String stafferId = nextLine[1];
                String departmentCode = nextLine[2];
                String departmentName = AllUserDataFilter.correctedDepartmentName(nextLine[2], departmentBRRepository);
                String stafferName = nextLine[3];
                String position = nextLine[5];
                String phoneType = AllUserDataFilter.correctedPhoneType(nextLine[7]);
                String phoneNumber = AllUserDataFilter.correctedPhoneNumber(nextLine[7]);
                String jobs = nextLine[13];

                if (AllUserDataFilter.allUserDataFilter(stafferName) && StringUtils.isNotBlank(departmentName)) {
                    // allUserData를 리스트에 저장
                    AllUserData allUserData = AllUserData.builder()
                        .stafferId(stafferId)
                        .departmentCode(departmentCode)
                        .departmentName(departmentName)
                        .stafferName(stafferName)
                        .position(position)
                        .phoneType(phoneType)
                        .phoneNumber(phoneNumber)
                        .jobs(jobs)
                        .build();
                    allUserDataSet.add(allUserData);

                    // csv 파일에 row 단위로 삽입
                    String[] row = {stafferId, departmentCode, departmentName, stafferName, position, phoneType,
                        phoneNumber, jobs};
                    correctedAllUserCsvWriter.writeNext(row);
                }
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }
    }
}
