package com.posicube.robi.reception.util.br;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.staffer.StafferJson.Phone;
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
public class PhoneBook {

    private String station;

    private String lastName;

    private String company;

    private String housePhone;


    public static List<PhoneBook> init() throws CsvValidationException {
        ClassPathResource phoneBookResource = new ClassPathResource("csv/br/rowData/phoneBook.csv");

        List<PhoneBook> series = new ArrayList<>();

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/correctedPhoneBook.csv";
        try (
            CSVReader csvReader = new CSVReader(new FileReader(phoneBookResource.getFile()));
            CSVWriter correctedPhoneBookWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            // column 명 제외
            csvReader.readNext();
            // column 명 포함
            correctedPhoneBookWriter.writeNext(new String[]{"station", "lastName", "company", "housePhone"});

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String station = nextLine[0];
                String lastName = nextLine[2];
                String company = nextLine[3];
                String housePhone = nextLine[4];

                PhoneBook phoneBook = PhoneBook.builder()
                    .station(station)
                    .lastName(lastName)
                    .company(company)
                    .housePhone(housePhone)
                    .build();
                series.add(phoneBook);

                String[] row = {station, lastName, company, housePhone};
                correctedPhoneBookWriter.writeNext(row);
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }

        return series;
    }
}
