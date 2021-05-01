package com.posicube.robi.reception.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.exception.JsonFileHandlingException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JsonUtil {

    private static String EXTENSION = ".json";

    @Value("${file.storage.json.local}")
    private String newJsonFilePath;

    private final ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        File folder = new File(this.newJsonFilePath);
        if (!folder.exists()) {
            try {
                FileUtils.forceMkdir(folder);
            } catch (IOException e) {
                throw new JsonFileHandlingException("Json Folder Create Error", e);
            }
        }
    }

    public File createJsonFile(String saveFileName, List<?> jsonList) {
        try {
            File file = new File(newJsonFilePath + saveFileName);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(newJsonFilePath + saveFileName + EXTENSION).toFile(), jsonList);
            return file;
        } catch (IOException e) {
            throw new JsonFileHandlingException("Cannot create Json file", e);
        }
    }

    public ByteArrayResource getByteArrayResource(List<?> jsonList, ObjectMapper objectMapper) {
        try {
            String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonList);
            final byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
            return new ByteArrayResource(bytes);
        } catch (IOException e) {
            throw new JsonFileHandlingException("Json File Create Error", e);
        }
    }
}
