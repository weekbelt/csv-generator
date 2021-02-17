package com.posicube.robi.reception.domain.br.staffer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.allUserData.AllUserData;
import com.posicube.robi.reception.domain.br.BRRepository;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.domain.br.phoneBook.PhoneBook;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

@Setter
@Getter
@Builder
public class BrStaffer {

    private Long id;

    private String firstName;

    private String lastName;

    private String title;

    private String phoneNumber;

    private String phoneTypeId;

    private String phoneTypeName;

    private String email;

    private String departmentId;

    private String departmentName;

    private String parentDepartmentId;

    private String parentDepartmentName;

    private String isAdmin;

    private String personalId;

    private String branchId;

    private String jobs;

    public static void initPhoneBookAllUserDataExceptDepartment(CsvReaderUtil csvReaderUtil, DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
        // phoneBook과 allUserData를 조합하여 BrStaffer를 생성
        ClassPathResource phoneBookResource = new ClassPathResource("csv/br/correctedData/correctedPhoneBook.csv");
        ClassPathResource departmentAllUserData = new ClassPathResource("csv/br/correctedData/departmentAllUserData.csv");
        List<String[]> phoneBookSeries = csvReaderUtil.convertCsvResourceToDataFrame(phoneBookResource);
        List<String[]> allUserDataSeries = csvReaderUtil.convertCsvResourceToDataFrame(departmentAllUserData);

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedBrStaffer.csv";
        String exceptCsvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/exceptCorrectedBrStaffer.csv";
        try (
            CSVWriter correctedBrStafferWriter = new CSVWriter(new FileWriter(csvFilePath));
            CSVWriter exceptBrStafferWriter = new CSVWriter(new FileWriter(exceptCsvFilePath));
        ) {
            correctedBrStafferWriter.writeNext(new String[]{"id", "firstName", "lastName", "title", "phoneNumber", "phoneTypeId", "phoneTypeName",
                "email", "departmentId", "departmentName", "parentDepartmentId", "parentDepartmentName", "isAdmin", "personalId", "branchId", "jobs"});
            exceptBrStafferWriter.writeNext(new String[]{"id", "firstName", "lastName", "title", "phoneNumber", "phoneTypeId", "phoneTypeName",
                "email", "departmentId", "departmentName", "parentDepartmentId", "parentDepartmentName", "isAdmin", "personalId", "branchId", "jobs"});

            for (String[] allUserData : allUserDataSeries) {
                String id = allUserData[0];
                String firstName = BrStafferFilter.correctedFirstName(allUserData[5]);
                String lastName = BrStafferFilter.correctedLastName(allUserData[5]);
                String title = "";
                String phoneNumber = "";
                String phoneTypeId = "";
                String phoneTypeName = "";
                String email = BrStafferFilter.correctedEmail(allUserData[1]);
                String departmentId = "";
                String departmentName = "";
                String parentDepartmentId = "";
                String parentDepartmentName = "";
                String isAdmin = BrStafferFilter.getAdmin(allUserData[4]);
                String personalId = allUserData[1];
                String branchId = "0dbf43a0-a168-11e9-a893-49ac915e00a7";
                String jobs = BrStafferFilter.correctedJobs(allUserData[9]);
                String[] phoneBook = getPhoneBook(allUserData, phoneBookSeries);
                if (phoneBook == null) {

                } else {
                    title = BrStafferFilter.correctedTitle(allUserData[6], phoneBook[3]);
                    String[] phoneInfo = BrStafferFilter.getPhoneInfo(allUserData[5], allUserData[7], allUserData[8], phoneBook[0]);
                    phoneNumber = phoneInfo[0];
                    phoneTypeId = phoneInfo[1];
                    phoneTypeName = phoneInfo[2];

                }
            }

//                String[] row = {String.valueOf(id), firstName, lastName, title, phoneNumber, phoneTypeId, phoneTypeName,
//                    departmentId, departmentName, parentDepartmentId, parentDepartmentName, parentDepartmentCode, email,
//                    isAdmin, personalId,
//                    branchId, jobs};
//                correctedBrStafferWriter.writeNext(row);
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file writing failed!!", e);
        }
    }

    private static String[] getPhoneBook(String[] allUserData, List<String[]> phoneBookSeries) {
        String allUserDataStafferName = allUserData[5];
        String allUserDataStafferDepartmentName = allUserData[4];

        for (String[] phoneBook : phoneBookSeries) {
            String phoneBookStafferName = phoneBook[1];
            String phoneBookDepartmentName = phoneBook[2];
            if (allUserDataStafferDepartmentName.equals(phoneBookDepartmentName) && allUserDataStafferName.equals(phoneBookStafferName)) {
                return phoneBook;
            }
        }
        return null;
    }

    private static String correctedJobs(String jobs) {
        List<String> jobList = new ArrayList<>();
        for (String job : jobs.split("[,.○]")) {
            job = job.trim();
            if (job.contains("@")) {
                String[] split = job.split("[@]");
                if (split.length >= 2) {
                    jobList.add(split[1]);
                }
            } else {
                jobList.add(job);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jobList.size(); i++) {
            if (i != jobList.size() - 1) {
                stringBuilder.append(jobList.get(i)).append(",");
            } else {
                stringBuilder.append(jobList.get(i));
            }
        }
        return stringBuilder.toString();
    }

    private static AllUserData getAllUserData(PhoneBook phoneBook, Set<AllUserData> allUserDataSet) {
        for (AllUserData allUserData : allUserDataSet) {
            if (allUserData.getDepartmentName().equals(phoneBook.getCompany()) &&
                allUserData.getStafferName().equals(phoneBook.getLastName())) {
                return allUserData;
            }
        }
        return AllUserData.builder().build();
    }

    private static String correctedFirstName(String name) {
        if (name.equals("팩스")) {
            return "";
        } else if (name.startsWith("남궁") || name.startsWith("독고") || name.startsWith("선우") ||
            name.startsWith("황보") || name.startsWith("황목")) {
            return name.substring(2);
        }

        return name.substring(1);
    }

    private static String correctedLastName(String name) {
        if (name.equals("팩스")) {
            return name;
        } else if (name.startsWith("남궁") || name.startsWith("독고") || name.startsWith("선우") ||
            name.startsWith("황보") || name.startsWith("황목")) {
            return name.substring(0, 2);
        }
        return name.substring(0, 1);
    }

    private static String correctedTitle(String position, String housePhone) {
        StringBuilder title = new StringBuilder();

        if (position.equals("시장") || position.equals("부시장") || position.equals("선장")) {
            return position;
        } else if (position.equals("과장") || position.equals("단장") || position.equals("국장") || position.equals("팀장")) {
            List<String> positions = new ArrayList<>();
            positions.add(position);
            positions.add(housePhone + "장");

            return makeTitle(title, positions);
        } else if (position.equals("동장")) {
            List<String> positions = new ArrayList<>();
            positions.add(position);
            positions.add(housePhone.substring(0, 4) + "장");

            return makeTitle(title, positions);
        } else if (position.equals("면장") || position.equals("읍장")) {
            List<String> positions = new ArrayList<>();
            positions.add(position);
            positions.add(housePhone.substring(0, 3) + "장");

            return makeTitle(title, positions);
        }
        return "";
    }

    private static String makeTitle(StringBuilder title, List<String> positions) {
        for (int i = 0; i < positions.size(); i++) {
            if (i != positions.size() - 1) {
                title.append(positions.get(i)).append(",");
            } else {
                title.append(positions.get(i));
            }
        }
        return title.toString();
    }

    private static String[] getPhoneInfo(String lastName, String phoneType, String phoneNumber, String station) {
        String[] phoneInfo = new String[3];
        if (lastName.equals("팩스")) {
            phoneInfo[0] = "041-" + station;
            phoneInfo[1] = "2";
            phoneInfo[2] = "외부국선";
        } else if (StringUtils.isBlank(phoneType) && StringUtils.isBlank(phoneNumber) && StringUtils
            .isNotBlank(station)) {
            if (station.equals("의사")) {
                phoneInfo[0] = "";
                phoneInfo[1] = "";
                phoneInfo[2] = "";
            } else if (station.length() != 4 && station.contains("930")) {
                phoneInfo[0] = station.substring(station.length() - 4);
                phoneInfo[1] = "1";
                phoneInfo[2] = "내선";
            } else if (station.length() != 4 && !station.contains("930")) {
                phoneInfo[0] = "041-" + station;
                phoneInfo[1] = "2";
                phoneInfo[2] = "외부국선";
            } else if (station.length() == 4) {
                phoneInfo[0] = station;
                phoneInfo[1] = "1";
                phoneInfo[2] = "내선";
            }
        } else if (StringUtils.isBlank(phoneType) && StringUtils.isBlank(phoneNumber) && StringUtils.isBlank(station)) {
            phoneInfo[0] = "";
            phoneInfo[1] = "";
            phoneInfo[2] = "";
        } else {
            phoneInfo[0] = phoneNumber;
            if (phoneType.equals("내선")) {
                phoneInfo[1] = "1";
            } else if (phoneType.equals("외부국선")) {
                phoneInfo[1] = "2";
            }
            phoneInfo[2] = phoneType;
        }
        return phoneInfo;
    }

    private static String correctedEmail(String stafferId) {
        return stafferId + "@generated.email";
    }

    private static String getAdmin(String lastName) {
        if (lastName.equals("정용일")) {
            return "1";
        }
        return "0";
    }
}
