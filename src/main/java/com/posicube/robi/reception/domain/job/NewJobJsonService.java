package com.posicube.robi.reception.domain.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Service
public class NewJobJsonService {

    private final ObjectMapper objectMapper;
    private final JsonUtil jsonUtil;
    private final JobRepository jobRepository;

    public Resource generateNewJob() throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<StafferJson> stafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/br/past/staffer.json"), new TypeReference<>() {
        });

        saveJobs(stafferJsonList);
        final List<Job> jobList = jobRepository.findAll();
        final List<NewJobJson> newJobJsonList = jobList.stream().map(job -> NewJobJson.builder()
            .id(UUID.randomUUID().toString())
            .name(job.getName().trim())
            .depth(1)
            .branchId(job.getBranchId())
            .jobType("DEFAULT")
            .synonyms(new ArrayList<>())
            .build()).collect(Collectors.toList());

        return jsonUtil.getByteArrayResource(newJobJsonList, objectMapper);
    }

    private void saveJobs(List<StafferJson> stafferJsonList) {
        stafferJsonList.forEach(stafferJson -> {
            final List<String> jobs = stafferJson.getJobs();
            jobs.forEach(jobName -> {
                String trimJob = jobName.trim();
                if (!jobRepository.existsByName(trimJob)) {
                    Job job = Job.builder()
                        .id(UUID.randomUUID().toString())
                        .name(trimJob)
                        .depth(1)
                        .branchId(stafferJson.getBchID())
                        .build();
                    jobRepository.save(job);
                }
            });
        });
    }
}
