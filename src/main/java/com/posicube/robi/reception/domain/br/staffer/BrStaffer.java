package com.posicube.robi.reception.domain.br.staffer;

import com.opencsv.CSVWriter;
import com.posicube.robi.reception.domain.br.AllUserData;
import com.posicube.robi.reception.domain.br.BRRepository;
import com.posicube.robi.reception.domain.br.Department;
import com.posicube.robi.reception.domain.br.PhoneBook;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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

    // department 관련부분은 나중에
    private String departmentId;

    private String departmentName;

    private String parentDepartmentId;

    private String parentDepartmentName;

    private String parentDepartmentCode;
    // 여기까지 나중에

    private String isAdmin;

    private String personalId;

    private String branchId;

    private String jobs;

    public static void initPhoneBookAllUserDataExceptDepartment(Set<PhoneBook> phoneBookSet,
        Set<AllUserData> allUserDataSet) {
        Set<BrStaffer> brStafferSet = BRRepository.brStafferSet;
        // phoneBook과 allUserData를 조합하여 BrStaffer를 생성
        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedBrStaffer.csv";
        try (CSVWriter correctedBrStafferWriter = new CSVWriter(new FileWriter(csvFilePath))) {
            correctedBrStafferWriter.writeNext(
                new String[]{"id", "firstName", "lastName", "title", "phoneNumber", "phoneTypeId", "phoneTypeName",
                    "departmentId", "departmentName", "parentDepartmentId", "parentDepartmentName",
                    "parentDepartmentCode", "email", "isAdmin",
                    "personalId", "branchId", "jobs"});

            Long stafferId = 1L;
            for (PhoneBook phoneBook : phoneBookSet) {
                AllUserData allUserData = getAllUserData(phoneBook, allUserDataSet);

                Long id = stafferId;
                stafferId++;

                String firstName = correctedFirstName(phoneBook.getLastName());
                String lastName = correctedLastName(phoneBook.getLastName());

                String title = "";
                if (allUserData.getPosition() != null && phoneBook.getHousePhone() != null) {
                    title = correctedTitle(allUserData.getPosition(), phoneBook.getHousePhone());
                }

                String[] phoneInfo = getPhoneInfo(phoneBook.getLastName(), allUserData.getPhoneType(),
                    allUserData.getPhoneNumber(), phoneBook.getStation());
                String phoneNumber = phoneInfo[0];
                String phoneTypeId = phoneInfo[1];
                String phoneTypeName = phoneInfo[2];

                String departmentId = "";
                String departmentName = phoneBook.getDepartmentName();
                String departmentCode = "";
                String parentDepartmentId = "";
                String parentDepartmentName = phoneBook.getCompany();
//                Map<String, Department> departmentMap = BRRepository.departmentMap;
//                String parentDepartmentName = departmentMap.get(allUserData.getDepartmentCode().trim()).getDepartmentName();
                String parentDepartmentCode = allUserData.getDepartmentCode();

                String email = correctedEmail(allUserData.getStafferId());
                String isAdmin = getAdmin(phoneBook.getLastName());
                String personalId = allUserData.getStafferId();
                String branchId = "0dbf43a0-a168-11e9-a893-49ac915e00a7";

                String jobs = "";
                if (allUserData.getJobs() != null) {
                    jobs = correctedJobs(allUserData.getJobs());
                }

                BrStaffer brStaffer = BrStaffer.builder()
                    .id(id)
                    .firstName(firstName)
                    .lastName(lastName)
                    .title(title)
                    .phoneNumber(phoneNumber)
                    .phoneTypeId(phoneTypeId)
                    .phoneTypeName(phoneTypeName)
                    .departmentId(departmentId)
                    .departmentName(departmentName)
                    .parentDepartmentId(parentDepartmentId)
                    .parentDepartmentName(parentDepartmentName)
                    .parentDepartmentCode(parentDepartmentCode)
                    .email(email)
                    .isAdmin(isAdmin)
                    .personalId(personalId)
                    .branchId(branchId)
                    .jobs(jobs)
                    .build();
                brStafferSet.add(brStaffer);

                String[] row = {String.valueOf(id), firstName, lastName, title, phoneNumber, phoneTypeId, phoneTypeName,
                    departmentId, departmentName, parentDepartmentId, parentDepartmentName, parentDepartmentCode, email,
                    isAdmin, personalId,
                    branchId, jobs};
                correctedBrStafferWriter.writeNext(row);
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file writing failed!!", e);
        }
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
