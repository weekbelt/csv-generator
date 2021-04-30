package com.posicube.robi.reception.domain.delta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Service
public class DeltaService {

    private final ObjectMapper objectMapper;

    public void generateDelta(String branchName) throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        List<StafferJson> newStafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/" + branchName + "/new/staffer.json"), new TypeReference<>() {
        });
        List<StafferJson> oldStafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/" + branchName + "/old/staffer.json"), new TypeReference<>() {
        });

        List<String> addedJobList = getAddedJobList(newStafferJsonList, oldStafferJsonList);
        List<StafferJson> changedStafferJsonList = getChangedStafferJson(newStafferJsonList, oldStafferJsonList);

        final String addedJobListResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(addedJobList);
        final String changedStafferJsonListResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(changedStafferJsonList);

        System.out.println(addedJobListResult);
        System.out.println();
        System.out.println(changedStafferJsonListResult);
    }

    private List<StafferJson> getChangedStafferJson(List<StafferJson> newStafferJsonList, List<StafferJson> oldStafferJsonList) {
        List<StafferJson> changedStafferJsonList = new ArrayList<>();
        newStafferJsonList.forEach(newStafferJson -> {
            if (!isSameStaffer(oldStafferJsonList, newStafferJson)) {
                changedStafferJsonList.add(newStafferJson);
            }
        });
        return changedStafferJsonList;
    }

    private boolean isSameStaffer(List<StafferJson> oldStafferJsonList, StafferJson newStafferJson) {
        String newStafferName = newStafferJson.getFullName();
        String newStafferPhoneNumber = newStafferJson.getPhone().getNumber();
        String newDepartmentName = newStafferJson.getDepartment();

        for (StafferJson oldStafferJson : oldStafferJsonList) {
            if (oldStafferJson.getFullName().equals(newStafferName) && oldStafferJson.getPhone().getNumber().equals(newStafferPhoneNumber) && oldStafferJson.getDepartment().equals(newDepartmentName)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getAddedJobList(List<StafferJson> newStafferJsonList, List<StafferJson> oldStafferJsonList) {
        Set<String> oldJobs = getJobs(oldStafferJsonList);
        Set<String> newJobs = getJobs(newStafferJsonList);
        return addedJobList(oldJobs, newJobs);
    }

    private Set<String> getJobs(List<StafferJson> stafferJsonList) {
        Set<String> jobs = new HashSet<>();
        stafferJsonList.forEach(stafferJson -> {
            jobs.addAll(stafferJson.getJobs());
        });
        return jobs;
    }

    private List<String> addedJobList(Set<String> oldJobs, Set<String> newJobs) {
        List<String> addedJobList = new ArrayList<>();
        newJobs.forEach(jobName -> {
            if (!oldJobs.contains(jobName)) {
                addedJobList.add(jobName);
            }
        });
        return addedJobList;
    }
}
