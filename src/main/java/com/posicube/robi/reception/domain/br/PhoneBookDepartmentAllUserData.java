package com.posicube.robi.reception.domain.br;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

public class PhoneBookDepartmentAllUserData {

    private static final CsvReaderUtil csvReaderUtil = new CsvReaderUtil();

    public static void initDepartmentAllUserData() throws CsvValidationException {
        ClassPathResource correctedPhoneBookResource = new ClassPathResource(
            "csv/br/correctedData/correctedPhoneBook.csv");
        List<String[]> correctedPhoneBookList = csvReaderUtil.convertCsvResourceToDataFrame(correctedPhoneBookResource);
        ClassPathResource departmentAllUserDataResource = new ClassPathResource(
            "csv/br/correctedData/departmentAllUserData.csv");
        List<String[]> departmentAllUserDataList = csvReaderUtil
            .convertCsvResourceToDataFrame(departmentAllUserDataResource);

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/phoneBookDepartmentAllUserData.csv";

        try (
            CSVWriter phoneBookDepartmentAllUserDataWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            phoneBookDepartmentAllUserDataWriter
                .writeNext(new String[]{"stafferId", "departmentCode", "departmentId", "departmentName",
                    "parentCode", "stafferName", "position", "phoneType", "phoneNumber", "jobs", "station",
                    "housePhone", "detailDepartmentName"});
            for (String[] departmentAllUserData : departmentAllUserDataList) {
                String[] phoneBook = getPhoneBook(departmentAllUserData, correctedPhoneBookList);
                String stafferId = departmentAllUserData[0];
                String departmentCode = departmentAllUserData[1];
                String departmentId = departmentAllUserData[2];
                String departmentName = departmentAllUserData[3];
                String parentCode = departmentAllUserData[4];
                String stafferName = departmentAllUserData[5];
                String position = departmentAllUserData[6];
                String phoneType = departmentAllUserData[7];
                String phoneNumber = departmentAllUserData[8];
                String jobs = departmentAllUserData[9];
                String station = phoneBook[0];
                String housePhone = phoneBook[3];
                String detailDepartmentName = phoneBook[4];

                String[] row = {stafferId, departmentCode, departmentId, departmentName, parentCode, stafferName,
                    position, phoneType, phoneNumber, jobs, station, housePhone, detailDepartmentName};
                phoneBookDepartmentAllUserDataWriter.writeNext(row);
            }

//            for (String[] phoneBook : correctedPhoneBookList) {
//                String[] departmentAllUserData = getDepartmentAllUserData(phoneBook, departmentAllUserDataList);
//
//                String stafferId = departmentAllUserData[0];
//                String departmentCode = departmentAllUserData[1];
//                String departmentId = departmentAllUserData[2];
//                String departmentName = departmentAllUserData[3];
//                String parentCode = departmentAllUserData[4];
//                String stafferName = departmentAllUserData[5];
//                String position = departmentAllUserData[6];
//                String phoneType = departmentAllUserData[7];
//                String phoneNumber = departmentAllUserData[8];
//                String jobs = departmentAllUserData[9];
//                String station = phoneBook[0];
//                String housePhone = phoneBook[3];
//                String detailDepartmentName = phoneBook[4];
//
//                String[] row = {stafferId, departmentCode, departmentId, departmentName, parentCode, stafferName,
//                    position, phoneType, phoneNumber, jobs, station, housePhone, detailDepartmentName};
//                phoneBookDepartmentAllUserDataWriter.writeNext(row);
//            }

        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }
    }

    private static String[] getPhoneBook(String[] departmentAllUserData, List<String[]> correctedPhoneBookList) {
        for (String[] phoneBook : correctedPhoneBookList) {
            if (departmentAllUserData[3].equals(phoneBook[2]) && departmentAllUserData[5].equals(phoneBook[1])) {
                return phoneBook;
            }
        }
        return new String[]{"", "", "", "", "", "", "", "", "", ""};
    }

    private static String[] getDepartmentAllUserData(String[] phoneBook,
        List<String[]> departmentAllUserDataList) {
        if (phoneBook[1].equals("팩스")) {
            return new String[]{"", "", "", phoneBook[2], "", phoneBook[1], "", "외부국선", "041-" + phoneBook[0], ""};
        }
        for (String[] departmentAllUserData : departmentAllUserDataList) {
            if (departmentAllUserData[3].equals(phoneBook[2]) && departmentAllUserData[5].equals(phoneBook[1])) {
                return departmentAllUserData;
            }
            if (departmentAllUserData[5].equals(phoneBook[1])) {
                return departmentAllUserData;
            }
        }
        return new String[]{"", "", "", "", "", "", "", "", "", ""};
    }
}
