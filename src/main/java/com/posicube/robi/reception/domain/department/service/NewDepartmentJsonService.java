package com.posicube.robi.reception.domain.department.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.department.DepartmentConverter;
import com.posicube.robi.reception.domain.department.dto.DepartmentId;
import com.posicube.robi.reception.domain.department.dto.DepartmentJson;
import com.posicube.robi.reception.domain.department.dto.NewDepartmentJson;
import com.posicube.robi.reception.domain.department.repository.DepartmentIdRepository;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
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
    private final DepartmentIdRepository departmentIdRepository;

    public Resource generateNewDepartment(String branchName) throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<DepartmentJson> departmentJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/" + branchName + "/old/department.json"), new TypeReference<>() {
        });

        // long 타입의 department id를 String 타입의 uuid로 바꾸기위해 저장
        saveDepartmentOldAndNewId(departmentJsonList);

        List<NewDepartmentJson> newDepartmentJsonList = departmentJsonList.stream().map(DepartmentConverter::convertToNewDepartmentJson).collect(Collectors.toList());

        // String 타입으로 바뀐 departmentId로 설정
        setNewDepartmentId(newDepartmentJsonList);

        return jsonUtil.getByteArrayResource(newDepartmentJsonList, objectMapper);
    }

    private void saveDepartmentOldAndNewId(List<DepartmentJson> departmentJsonList) {
        departmentJsonList.forEach(departmentJson -> {
            DepartmentId departmentId = DepartmentId.builder()
                .oldDepartmentId(departmentJson.getDepartmentID())
                .newDepartmentId(UUID.randomUUID().toString())
                .build();
            departmentIdRepository.save(departmentId);
        });
    }

    private void setNewDepartmentId(List<NewDepartmentJson> newDepartmentJsonList) {
        newDepartmentJsonList.forEach(newDepartmentJson -> {
            setNewDepartmentId(newDepartmentJson);
            setNewParentDepartmentId(newDepartmentJson);
        });
    }

    private void setNewDepartmentId(NewDepartmentJson newDepartmentJson) {
        DepartmentId departmentId = departmentIdRepository.findByOldDepartmentId(Long.valueOf(newDepartmentJson.getId()))
            .orElseThrow(() -> new EntityNotFoundException("해당하는 departmentId를 찾지 못했습니다. oldDepartmentId=" + newDepartmentJson.getId()));
        newDepartmentJson.setNewDepartmentId(departmentId.getNewDepartmentId());
    }

    private void setNewParentDepartmentId(NewDepartmentJson newDepartmentJson) {
        Optional.ofNullable(newDepartmentJson.getParentDept()).ifPresent(parentDept -> {
            DepartmentId parentDepartmentId = departmentIdRepository.findByOldDepartmentId(Long.valueOf(parentDept.getId()))
                .orElseThrow(() -> new EntityNotFoundException("해당하는 departmentId를 찾지 못했습니다. oldDepartmentId=" + parentDept.getId()));
            parentDept.setNewParentDepartmentId(parentDepartmentId.getNewDepartmentId());
        });
    }
}
