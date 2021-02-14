package com.posicube.robi.reception.domain.br;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

@Builder
@Getter
@Setter
public class PhoneBook {

    private String station;

    private String lastName;

    private String company;

    private String housePhone;

    private String departmentName;

    public static void init() throws CsvValidationException {
        ClassPathResource phoneBookResource = new ClassPathResource("csv/br/rowData/phoneBook.csv");

//        List<PhoneBook> series = new ArrayList<>();
        Set<PhoneBook> phoneBookSet = BRRepository.phoneBookSet;

        String correctedCsvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedPhoneBook.csv";
        String exceptedCsvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/exceptedPhoneBook.csv";
        try (
            CSVReader csvReader = new CSVReader(new FileReader(phoneBookResource.getFile()));
            CSVWriter correctedPhoneBookWriter = new CSVWriter(new FileWriter(correctedCsvFilePath));
            CSVWriter exceptedPhoneBookWriter = new CSVWriter(new FileWriter(exceptedCsvFilePath))
        ) {
            // column 명 제외
            csvReader.readNext();
            // column 명 포함
            correctedPhoneBookWriter
                .writeNext(new String[]{"station", "lastName", "company", "housePhone", "departmentName"});

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String station = nextLine[0].trim();
                String lastName = correctLastName(nextLine[2], nextLine[4]).trim();
                String company = nextLine[3].trim();
                String housePhone = nextLine[4].trim();
                String departmentName = correctedDepartmentName(company, housePhone).trim();

                // 빈 row 입력 x
                if (StringUtils.isNotBlank(lastName) && hasDepartment(company)) {
                    PhoneBook phoneBook = PhoneBook.builder()
                        .station(station)
                        .lastName(lastName)
                        .company(company)
                        .housePhone(housePhone)
                        .departmentName(departmentName)
                        .build();

                    boolean isUnique = true;
                    for (PhoneBook element : phoneBookSet) {
                        if (element.getLastName().equals(phoneBook.getLastName()) &&
                            element.getCompany().equals(phoneBook.getCompany())) {
                            isUnique = false;
                            break;
                        }
                    }

                    if (isUnique) {
                        phoneBookSet.add(phoneBook);

                        String[] row = {station, lastName, company, housePhone, departmentName};
                        correctedPhoneBookWriter.writeNext(row);
                    }
                } else {
                    String[] row = {station, lastName, company, housePhone, departmentName};
                    exceptedPhoneBookWriter.writeNext(row);
                }
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }
    }

    public static String correctLastName(String lastName, String housePhone) {
        if (lastName.contains("fax") || lastName.contains("FAX") || lastName.contains("FXA") || housePhone
            .contains("FAX")) {
            return "팩스";
        } else if (lastName.endsWith("팀장") || lastName.endsWith("공익")) {
            return lastName.substring(0, lastName.length() - 2).trim();
        } else if (lastName.startsWith("관제센타")) {
            return lastName.substring(4);
        }
        return lastName;
    }

    public static String correctedDepartmentName(String company, String housePhone) {
        if (housePhone.equals("의장") || housePhone.equals("부의장") || housePhone.equals("의원") || housePhone.endsWith("위원장")
            || housePhone.equals("선장") || StringUtils.isBlank(housePhone)) {
            return company;
        } else if (housePhone.equals("부시장")) {
            return "부시장실";
        } else if (housePhone.equals("시장") | housePhone.equals("비서실장") || housePhone.equals("민원보좌관") || housePhone
            .equals("수행비서")) {
            return "시장실";
        } else if (housePhone.equals("농업기술센터소장") || housePhone.equals("보건소장실")) {
            return housePhone.substring(0, housePhone.length() - 2);
        } else if (housePhone.endsWith("동장")) {
            return housePhone.substring(0, housePhone.length() - 1) + "주민센터";
        } else if (housePhone.endsWith("면장") || housePhone.endsWith("읍장")) {
            return housePhone.substring(0, housePhone.length() - 1) + "사무소";
        } else if (housePhone.endsWith("장")) {
            return housePhone.substring(0, housePhone.length() - 1);
        } else if (housePhone.equals("공중보건의사")) {
            return "진료팀";
        } else if (housePhone.equals("전문위원")) {
            return "의회사무국";
        }
        return housePhone;
    }

    public static boolean hasDepartment(String company) {
        Map<String, Department> departmentMap = BRRepository.departmentMap;
        Set<String> departmentCodeSet = departmentMap.keySet();
        for (String departmentCode : departmentCodeSet) {
            Department department = departmentMap.get(departmentCode);
            if (department.getDepartmentName().equals(company)) {
                return true;
            }
        }
        return false;
    }

}
