package com.posicube.robi.reception.domain.br.phoneBook;

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
public class PhoneBook {

    private String station;

    private String lastName;

    private String company;

    private String housePhone;

    private String departmentName;

    public static void init(DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
        ClassPathResource phoneBookResource = new ClassPathResource("csv/br/rowData/phoneBook.csv");

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
            exceptedPhoneBookWriter
                .writeNext(new String[]{"station", "lastName", "company", "housePhone", "departmentName"});

            String[] nextLine;
            Set<PhoneBook> phoneBookSet = BRRepository.phoneBookSet;
            while ((nextLine = csvReader.readNext()) != null) {
                String station = nextLine[0].trim();
                String lastName = PhoneBookFilter.correctLastName(nextLine[2], nextLine[4]).trim();
                String company = nextLine[3].trim();
                String housePhone = nextLine[4].trim();
                String departmentName = PhoneBookFilter.correctedDepartmentName(company, housePhone).trim();

                // 빈 row 입력 x
                if (StringUtils.isNotBlank(lastName) && departmentBRRepository.existsByDepartmentName(company)) {
                    PhoneBook phoneBook = PhoneBook.builder()
                        .station(station)
                        .lastName(lastName)
                        .company(company)
                        .housePhone(housePhone)
                        .departmentName(departmentName)
                        .build();

                    if (PhoneBookFilter.isUnique(phoneBookSet, phoneBook)) {
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
}
