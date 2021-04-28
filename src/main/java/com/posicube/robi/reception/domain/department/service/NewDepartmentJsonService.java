package com.posicube.robi.reception.domain.department.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.department.DepartmentJson;
import com.posicube.robi.reception.domain.department.NewDepartmentJson;
import com.posicube.robi.reception.domain.department.NewDepartmentJson.ParentDept;
import com.posicube.robi.reception.domain.department.NewDepartmentJson.Phone;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.ArrayList;
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

    public Resource generateNewDepartment() throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<DepartmentJson> departmentJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/br/past/department.json"), new TypeReference<>() {
        });

        final List<NewDepartmentJson> newDepartmentJsonList = departmentJsonList.stream().map(departmentJson -> {
            final Phone phone = createPhone(departmentJson);
            final ParentDept parentDept = createParentDept(departmentJson);
            return NewDepartmentJson.builder()
                .id(String.valueOf(departmentJson.getDepartmentID()))
                .name(departmentJson.getDeptName())
                .phone(phone)
                .parentDept(parentDept)
                .branchID(departmentJson.getBchID())
                .synonyms(new ArrayList<>())
                .build();
        }).collect(Collectors.toList());

        return JsonUtil.getByteArrayResource(newDepartmentJsonList, objectMapper);
    }

    private NewDepartmentJson.Phone createPhone(DepartmentJson departmentJson) {
        if (departmentJson.getPhone() != null) {
            try {
                Phone phone = objectMapper.treeToValue(departmentJson.getPhone(), Phone.class);
                return NewDepartmentJson.Phone.builder()
                    .number(phone.getNumber())
                    .type("INWARD_DIALING")
                    .build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private NewDepartmentJson.ParentDept createParentDept(DepartmentJson departmentJson) {
        if (departmentJson.getParentDept() != null) {
            final Phone phone = Phone.builder()
                .number(departmentJson.getParentDept().getPhone().getNumber())
                .type("INWARD_DIALING")
                .build();
            return NewDepartmentJson.ParentDept.builder()
                .id(String.valueOf(departmentJson.getParentDept().getId()))
                .name(departmentJson.getParentDept().getName())
                .phone(phone)
                .build();
        }
        return null;
    }
}
