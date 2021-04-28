package com.posicube.robi.reception.domain.staffer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.NewStafferJson;
import com.posicube.robi.reception.domain.staffer.NewStafferJson.Phone;
import com.posicube.robi.reception.domain.staffer.StafferJson;
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
public class NewStafferJsonService {

    private final ObjectMapper objectMapper;

    public Resource generateNewStaffer() throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<StafferJson> stafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/br/past/staffer.json"), new TypeReference<>() {
        });

        List<NewStafferJson> newStafferJsonList = stafferJsonList.stream().map(stafferJson -> {
            Phone phone = createPhone(stafferJson);
            Phone newPhone2 = createPhone2(stafferJson);

            List<String> jobs = new ArrayList<>();
            stafferJson.getJobs().forEach(job -> jobs.add(job.trim()));

            return NewStafferJson.builder()
                .id(String.valueOf(stafferJson.getStaffID()))
                .name(stafferJson.getFullName())
                .phone(phone)
                .phone2(newPhone2)
                .departmentId(String.valueOf(stafferJson.getDepartmentID()))
                .departmentName(stafferJson.getDepartment())
                .jobs(jobs)
                .positions(stafferJson.getPositions())
                .branchId(stafferJson.getBchID())
                .build();
        }).collect(Collectors.toList());

        return JsonUtil.getByteArrayResource(newStafferJsonList, objectMapper);
    }

    private Phone createPhone(StafferJson stafferJson) {
        return Phone.builder()
            .number(stafferJson.getPhone().getNumber())
            .type(stafferJson.getPhone().getType().equals("내선") ? "INWARD_DIALING" : "OUTWARD_DIALING")
            .build();
    }

    private Phone createPhone2(StafferJson stafferJson) {
        if (stafferJson.getPhone2() != null) {
            try {
                final Phone phone2 = objectMapper.treeToValue(stafferJson.getPhone2(), Phone.class);
                return NewStafferJson.Phone.builder()
                    .type("INWARD_DIALING")
                    .number(phone2.getNumber() == null ? "0000" : phone2.getNumber())
                    .build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
