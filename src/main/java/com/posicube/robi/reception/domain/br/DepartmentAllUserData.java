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


public class DepartmentAllUserData {

    private static final CsvReaderUtil csvReaderUtil = new CsvReaderUtil();

    public static void initDepartmentAllUserData() throws CsvValidationException {
        ClassPathResource correctedAllUserDataResource = new ClassPathResource("csv/br/correctedData/correctedAllUserData.csv");
        List<String[]> allUserDataList = csvReaderUtil.convertCsvResourceToDataFrame(correctedAllUserDataResource);

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/departmentAllUserData.csv";

        try (
            CSVWriter departmentAllUserDataCsvWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            departmentAllUserDataCsvWriter
                .writeNext(new String[]{"stafferId", "departmentCode", "departmentId", "departmentName",
                    "parentCode", "stafferName", "position", "phoneType", "phoneNumber", "jobs"});
            Map<String, Department> departmentMap = BRRepository.departmentMap;
            for (String[] allUserData : allUserDataList) {
                String stafferId = allUserData[0];
                String departmentCode = allUserData[1];
                Department department = departmentMap.get(departmentCode);
                String departmentId = department.getDepartmentId();
                String departmentName = department.getDepartmentName();
                String parentCode = department.getParentCode();
                String stafferName = allUserData[3];
                String position = allUserData[4];
                String phoneType = allUserData[5];
                String phoneNumber = allUserData[6];
                String jobs = allUserData[7];

                String[] row = {stafferId, departmentCode, departmentId, departmentName, parentCode, stafferName,
                    position, phoneType, phoneNumber, jobs};
                departmentAllUserDataCsvWriter.writeNext(row);
            }

        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }
    }
}
