package com.posicube.robi.reception.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.department.DepartmentJson;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.exception.JsonFileHandlingException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;

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

    public static ByteArrayResource getByteArrayResource(List<?> jsonList, ObjectMapper objectMapper) {
        try {
            String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonList);
            final byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
            return new ByteArrayResource(bytes);
        } catch (IOException e) {
            throw new JsonFileHandlingException("Json File Create Error", e);
        }
    }
}
