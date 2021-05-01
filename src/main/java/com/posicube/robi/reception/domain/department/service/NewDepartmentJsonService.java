package com.posicube.robi.reception.domain.department.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.department.DepartmentConverter;
import com.posicube.robi.reception.domain.department.DepartmentJson;
import com.posicube.robi.reception.domain.department.NewDepartmentJson;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Transactional
@Service
public class NewDepartmentJsonService {

    private final ObjectMapper objectMapper;
    private final JsonUtil jsonUtil;

    public Resource generateNewDepartment(String branchName) throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<DepartmentJson> departmentJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/" + branchName + "/old/department.json"), new TypeReference<>() {
        });

        List<NewDepartmentJson> newDepartmentJsonList = departmentJsonList.stream().map(DepartmentConverter::convertToNewDepartmentJson).collect(Collectors.toList());

        return jsonUtil.getByteArrayResource(newDepartmentJsonList, objectMapper);
    }
}
