package com.posicube.robi.reception.domain.staffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.NewStafferJson.Phone;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StafferConverter {

    public static NewStafferJson convertToNewStaffer(StafferJson stafferJson) {
        ObjectMapper objectMapper = new ObjectMapper();

        Phone phone = createPhone(stafferJson);
        Phone newPhone2 = createPhone2(stafferJson, objectMapper);
        List<String> jobs = createJobs(stafferJson);

        return NewStafferJson.builder()
            .id(UUID.randomUUID().toString())
            .name(stafferJson.getFullName())
            .phone(phone)
            .phone2(newPhone2)
            .departmentId(String.valueOf(stafferJson.getDepartmentID()))
            .departmentName(stafferJson.getDepartment())
            .jobs(jobs)
            .positions(stafferJson.getPositions())
            .branchId(stafferJson.getBchID())
            .build();
    }

    private static Phone createPhone(StafferJson stafferJson) {
        return Phone.builder()
            .number(stafferJson.getPhone().getNumber())
            .type(stafferJson.getPhone().getType().equals("내선") ? "INWARD_DIALING" : "OUTWARD_DIALING")
            .build();
    }

    private static Phone createPhone2(StafferJson stafferJson, ObjectMapper objectMapper) {
        Optional<JsonNode> jsonPhone2 = Optional.ofNullable(stafferJson.getPhone2());
        return jsonPhone2.map(phone2JsonNode -> {
            Phone phone2 = getOldStafferPhone2(stafferJson, objectMapper);
            return NewStafferJson.Phone.builder()
                .type("INWARD_DIALING")
                .number(phone2.getNumber() == null ? "0000" : phone2.getNumber())
                .build();
        }).orElse(null);
    }

    private static Phone getOldStafferPhone2(StafferJson stafferJson, ObjectMapper objectMapper) {
        Phone phone2 = null;
        try {
            phone2 = objectMapper.treeToValue(stafferJson.getPhone2(), Phone.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return phone2;
    }

    private static List<String> createJobs(StafferJson stafferJson) {
        List<String> jobs = new ArrayList<>();
        stafferJson.getJobs().forEach(job -> jobs.add(job.trim()));
        return jobs;
    }
}
