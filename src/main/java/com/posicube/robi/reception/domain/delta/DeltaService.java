package com.posicube.robi.reception.domain.delta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Service
public class DeltaService {

    private final ObjectMapper objectMapper;

    public List<StafferJson> generateDelta() throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        List<StafferJson> changeNewStafferJsonList = new ArrayList<>();
        List<StafferJson> notChangedStafferJsonList = new ArrayList<>();

        List<StafferJson> newStafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/gbdc/new/staffer.json"), new TypeReference<List<StafferJson>>() {
        });
        List<StafferJson> oldStafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/gbdc/old/staffer.json"), new TypeReference<List<StafferJson>>() {
        });

        for (StafferJson newStafferJson : newStafferJsonList) {
            String currentStafferName = newStafferJson.getFullName();
            String currentStafferDepartmentName = newStafferJson.getDepartment();
            String currentStafferPhoneNumber = newStafferJson.getPhone().getNumber();

            for (StafferJson oldStafferJson : oldStafferJsonList) {
                String oldStafferName = oldStafferJson.getFullName();
                String oldStafferDepartmentName = oldStafferJson.getDepartment();
                String oldStafferPhoneNumber = oldStafferJson.getPhone().getNumber();

                if (currentStafferName.equals(oldStafferName)
                    && currentStafferDepartmentName.equals(oldStafferDepartmentName)
                    && currentStafferPhoneNumber.equals(oldStafferPhoneNumber)) {
                    notChangedStafferJsonList.add(newStafferJson);
                    break;
                }
            }

        }

        newStafferJsonList.removeAll(notChangedStafferJsonList);

        System.out.println(newStafferJsonList.size());

        return newStafferJsonList;
    }
}
