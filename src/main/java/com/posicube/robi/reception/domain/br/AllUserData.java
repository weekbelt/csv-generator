package com.posicube.robi.reception.domain.br;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
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
                String departmentName = correctedDepartmentName(nextLine[2], departmentBRRepository);
                String stafferName = nextLine[3];
                String position = nextLine[5];
                String phoneType = correctedPhoneType(nextLine[7]);
                String phoneNumber = correctedPhoneNumber(nextLine[7]);
                String jobs = nextLine[13];

                if (allUserDataFilter(stafferName) && StringUtils.isNotBlank(departmentName)) {
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

    private static String correctedDepartmentName(String departmentCode, DepartmentBRRepository departmentBRRepository) {
        Optional<DepartmentBR> optionalDepartmentBR = departmentBRRepository.findDepartmentBRByDepartmentCode(departmentCode);
        if (optionalDepartmentBR.isPresent()) {
            DepartmentBR departmentBR = optionalDepartmentBR.get();
            return departmentBR.getDepartmentName();
        }
        return "";
    }

    public static boolean allUserDataFilter(String stafferName) {
        return !stafferName.startsWith("gamsa") && !stafferName.startsWith("감사") && !stafferName.equals("고용복지+센터");
    }

    public static String correctedPhoneType(String phoneNumber) {
        if (phoneNumber.length() == 4) {
            return "내선";
        } else if (phoneNumber.length() == 10) {
            if (phoneNumber.contains("041930")) {
                return "내선";
            } else {
                return "외부국선";
            }
        } else if (phoneNumber.length() == 7) {
            if (phoneNumber.startsWith("930")) {
                return "내선";
            } else {
                return "외부국선";
            }
        }
        return "";
    }

    public static String correctedPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 4) {
            return phoneNumber;
        } else if (phoneNumber.length() == 10) {
            if (phoneNumber.contains("041930")) {
                return phoneNumber.substring(phoneNumber.length() - 4);
            } else {
                return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
            }
        } else if (phoneNumber.length() == 7) {
            if (phoneNumber.startsWith("930")) {
                return phoneNumber.substring(phoneNumber.length() - 4);
            } else {
                return "041-" + phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3);
            }
        }
        return phoneNumber;
    }
}
