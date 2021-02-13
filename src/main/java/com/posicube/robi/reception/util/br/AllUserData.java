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
public class AllUserData {

    private String stafferId;

    private String departmentCode;

    private String stafferName;

    private String position;

    private String phoneNumber;

    private String jobs;

    public static  List<AllUserData> init() throws CsvValidationException {
        ClassPathResource allUserDataResource = new ClassPathResource("csv/br/rowData/allUserData.csv");

        List<AllUserData> series = new ArrayList<>();

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedAllUserData.csv";
        try (
            CSVReader csvReader = new CSVReader(new FileReader(allUserDataResource.getFile()));
            CSVWriter correctedAllUserCsvWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            // column 명 제외
            csvReader.readNext();
            // column 명 포함
            correctedAllUserCsvWriter.writeNext(new String[]{"stafferId", "departmentCode", "stafferName", "position", "phoneNumber", "jobs"});

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String stafferId = nextLine[1];
                String departmentCode = nextLine[2];
                String stafferName = nextLine[3];
                String position = nextLine[5];
                String phoneNumber = nextLine[7];
                String jobs = nextLine[13];

                if (!stafferName.contains("gamsa") && !stafferName.contains("감사")) {
                    // allUserData를 리스트에 저장
                    AllUserData allUserData = AllUserData.builder()
                        .stafferId(stafferId)
                        .departmentCode(departmentCode)
                        .stafferName(stafferName)
                        .position(position)
                        .phoneNumber(phoneNumber)
                        .jobs(jobs)
                        .build();
                    series.add(allUserData);

                    // csv 파일에 row 단위로 삽입
                    String[] row = {stafferId, departmentCode, stafferName, position, phoneNumber, jobs};
                    correctedAllUserCsvWriter.writeNext(row);
                }
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }

        return series;
    }


}
