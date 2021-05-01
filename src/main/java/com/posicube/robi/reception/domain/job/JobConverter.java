package com.posicube.robi.reception.domain.job;

import com.posicube.robi.reception.domain.staffer.StafferJson;
import java.util.ArrayList;
import java.util.UUID;

public class JobConverter {

    public static NewJobJson convertToNewJobJsonFromJob(Job job) {
        return NewJobJson.builder()
            .id(UUID.randomUUID().toString())
            .name(job.getName().trim())
            .depth(1)
            .branchId(job.getBranchId())
            .jobType("DEFAULT")
            .synonyms(new ArrayList<>())
            .build();
    }

    public static Job convertToJob(StafferJson stafferJson, String job) {
        return Job.builder()
            .id(UUID.randomUUID().toString())
            .name(job)
            .depth(1)
            .branchId(stafferJson.getBchID())
            .build();
    }
}
