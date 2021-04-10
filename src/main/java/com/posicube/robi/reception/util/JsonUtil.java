package com.posicube.robi.reception.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.department.DepartmentJson;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    public static File createDepartmentJsonFile(String filePath, String saveFileName, List<DepartmentJson> departmentJsonList, ObjectMapper objectMapper) throws IOException {
        try {
            File file = new File(filePath + saveFileName);
            objectMapper.writeValue(Paths.get(filePath + saveFileName).toFile(), departmentJsonList);
            return file;
        } catch (IOException e) {
            throw new IOException("Cannot save DepartmentJson file", e);
        }
    }

    public static File createStafferJsonFile(String filePath, String saveFileName, List<StafferJson> stafferJsonList, ObjectMapper objectMapper) throws IOException {
        try {
            File file = new File(filePath + saveFileName);
            objectMapper.writeValue(Paths.get(filePath + saveFileName).toFile(), stafferJsonList);
            return file;
        } catch (IOException e) {
            throw new IOException("Cannot save Staffer file", e);
        }
    }
}
