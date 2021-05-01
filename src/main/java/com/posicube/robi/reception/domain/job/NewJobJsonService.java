package com.posicube.robi.reception.domain.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Service
public class NewJobJsonService {

    private final ObjectMapper objectMapper;
    private final JsonUtil jsonUtil;
    private final JobRepository jobRepository;

    public Resource generateNewJob(String branchName) throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<StafferJson> stafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/" + branchName + "/old/staffer.json"), new TypeReference<>() {
        });

        saveJobs(stafferJsonList);
        List<NewJobJson> newJobJsonList = getNewJobJsonList();
        return jsonUtil.getByteArrayResource(newJobJsonList, objectMapper);
    }

    private void saveJobs(List<StafferJson> stafferJsonList) {
        stafferJsonList.forEach(stafferJson -> {
            stafferJson.getJobs().forEach(jobName -> {
                saveJobIfNotExists(stafferJson, jobName);
            });
        });
    }

    private void saveJobIfNotExists(StafferJson stafferJson, String jobName) {
        String trimJob = jobName.trim();
        if (!jobRepository.existsByName(trimJob) && StringUtils.isNotBlank(trimJob)) {
            Job job = JobConverter.convertToJob(stafferJson, trimJob);
            jobRepository.save(job);
        }
    }

    private List<NewJobJson> getNewJobJsonList() {
        List<Job> jobList = jobRepository.findAll();
        return jobList.stream().map(JobConverter::convertToNewJobJsonFromJob).collect(Collectors.toList());
    }
}
