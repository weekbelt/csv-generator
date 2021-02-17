package com.posicube.robi.reception.domain.br.staffer;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
                    String[] row = {id, firstName, lastName, title, phoneNumber, phoneTypeId, phoneTypeName,
                        email, departmentId, departmentName, parentDepartmentId, parentDepartmentName, isAdmin,
                        personalId, branchId, jobs};
                    exceptBrStafferWriter.writeNext(row);
                } else {
                    title = BrStafferFilter.correctedTitle(allUserData[6], phoneBook[3]);
                    String[] phoneInfo = BrStafferFilter.getPhoneInfo(allUserData[5], allUserData[7], allUserData[8], phoneBook[0]);
                    phoneNumber = phoneInfo[0];
                    phoneTypeId = phoneInfo[1];
                    phoneTypeName = phoneInfo[2];

                    // department
                    DepartmentBR departmentBR = BrStafferFilter.getDepartmentBR(allUserData[4], allUserData[2], allUserData[3], phoneBook[4], departmentBRRepository);
                    departmentId = String.valueOf(departmentBR.getDepartmentId());
                    departmentName = departmentBR.getDepartmentName();

                    // parentDepartment
                    DepartmentBR parentDepartmentBR = departmentBRRepository.findDepartmentBRByDepartmentCode(departmentBR.getParentCode())
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 부서가 존재하지 않습니다. 부서코드: " + departmentBR.getParentCode()));
                    parentDepartmentId = String.valueOf(parentDepartmentBR.getDepartmentId());
                    parentDepartmentName = parentDepartmentBR.getDepartmentName();

                    String[] row = {id, firstName, lastName, title, phoneNumber, phoneTypeId, phoneTypeName,
                        email, departmentId, departmentName, parentDepartmentId, parentDepartmentName, isAdmin,
                        personalId, branchId, jobs};
                    correctedBrStafferWriter.writeNext(row);
                }
            }

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
}
