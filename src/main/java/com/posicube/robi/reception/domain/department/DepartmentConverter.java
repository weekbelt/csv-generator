package com.posicube.robi.reception.domain.department;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.department.NewDepartmentJson.ParentDept;
import com.posicube.robi.reception.domain.department.NewDepartmentJson.Phone;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentConverter {

    public static NewDepartmentJson convertToNewDepartmentJson(DepartmentJson departmentJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        Phone phone = createPhone(departmentJson, objectMapper);
        ParentDept parentDept = createParentDept(departmentJson);

        return NewDepartmentJson.builder()
            .id(String.valueOf(departmentJson.getDepartmentID()))
            .name(departmentJson.getDeptName())
            .phone(phone)
            .parentDept(parentDept)
            .branchID(departmentJson.getBchID())
            .synonyms(new ArrayList<>())
            .build();
    }

    private static Phone createPhone(DepartmentJson departmentJson, ObjectMapper objectMapper) {
        Optional<JsonNode> phone = Optional.ofNullable(departmentJson.getPhone());
        return phone.map(phoneJsonNode -> {
            Phone oldDepartmentPhone = getOldDepartmentPhone(objectMapper, phoneJsonNode);
            return NewDepartmentJson.Phone.builder()
                .type(Objects.requireNonNull(oldDepartmentPhone).getType())
                .number(oldDepartmentPhone.getNumber())
                .build();
        }).orElse(null);
    }

    private static Phone getOldDepartmentPhone(ObjectMapper objectMapper, JsonNode phoneJsonNode) {
        Phone oldDepartmentPhone = null;
        try {
            oldDepartmentPhone = objectMapper.treeToValue(phoneJsonNode, Phone.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oldDepartmentPhone;
    }

    private static ParentDept createParentDept(DepartmentJson departmentJson) {
        Optional<DepartmentJson.ParentDept> optionalParentDept = Optional.ofNullable(departmentJson.getParentDept());
        return optionalParentDept.map(parentDept -> {
            Phone phone = Phone.builder()
                .number(departmentJson.getParentDept().getPhone().getNumber())
                .type("INWARD_DIALING")
                .build();
            return NewDepartmentJson.ParentDept.builder()
                .id(String.valueOf(departmentJson.getParentDept().getId()))
                .name(departmentJson.getParentDept().getName())
                .phone(phone)
                .build();
        }).orElse(null);
    }

}
