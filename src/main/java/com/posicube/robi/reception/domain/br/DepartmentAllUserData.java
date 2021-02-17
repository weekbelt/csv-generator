package com.posicube.robi.reception.domain.br;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;


public class DepartmentAllUserData {

    private static final CsvReaderUtil csvReaderUtil = new CsvReaderUtil();

    public static void initDepartmentAllUserData(DepartmentBRRepository departmentBRRepository) throws CsvValidationException {
        ClassPathResource correctedAllUserDataResource = new ClassPathResource("csv/br/correctedData/correctedAllUserData.csv");
        List<String[]> allUserDataList = csvReaderUtil.convertCsvResourceToDataFrame(correctedAllUserDataResource);

        String csvFilePath = "/Users/joohyuk/Documents/SPRINGWORKSPACE/2021Project/reception-backend-directory-generator/src/main/resources/csv/br/correctedData/departmentAllUserData.csv";

        try (
            CSVWriter departmentAllUserDataCsvWriter = new CSVWriter(new FileWriter(csvFilePath))
        ) {
            departmentAllUserDataCsvWriter
                .writeNext(new String[]{"id", "personalId", "departmentCode", "departmentId", "departmentName",
                    "stafferName", "position", "phoneType", "phoneNumber", "jobs"});

            Long stafferId = 1L;
            for (String[] allUserData : allUserDataList) {
                Long id = stafferId;
                String personalId = allUserData[0];

                String departmentCode = allUserData[1];
//                Department department = departmentMap.get(departmentCode);
                DepartmentBR departmentBR = departmentBRRepository.findDepartmentBRByDepartmentCode(departmentCode)
                    .orElseThrow(() -> new IllegalArgumentException("부서코드에 해당하는 부서가 존재하지 않습니다. departmentCode: " + departmentCode));
                Long departmentId = departmentBR.getDepartmentId();
                String departmentName = departmentBR.getDepartmentName();

                String stafferName = allUserData[3];
                String position = allUserData[4];
                String phoneType = allUserData[5];
                String phoneNumber = allUserData[6];
                String jobs = allUserData[7];

                String[] row = {String.valueOf(id), personalId, departmentCode, String.valueOf(departmentId),
                    departmentName, stafferName, position, phoneType, phoneNumber, jobs};
                departmentAllUserDataCsvWriter.writeNext(row);

                stafferId++;
            }

        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }
    }
}
