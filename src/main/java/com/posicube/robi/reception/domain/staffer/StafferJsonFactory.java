package com.posicube.robi.reception.domain.staffer;

import com.fasterxml.jackson.databind.JsonNode;
import com.posicube.robi.reception.domain.department.dto.DepartmentSeries;
import com.posicube.robi.reception.domain.staffer.StafferJson.Department;
import java.util.List;
import java.util.UUID;

public class StafferJsonFactory {

    public static StafferJson createStafferJson(StafferSeries stafferSeries, StafferJson.Phone phone, DepartmentSeries departmentSeries, JsonNode phone2, List<String> jobs, List<String> positions, List<Department> hierarchyList) {
        return StafferJson.builder()
            .staffID(stafferSeries.getId())
            .firstName(stafferSeries.getFirstName())
            .lastName(stafferSeries.getLastName())
            .fullName(stafferSeries.getFullName())
            .phone(phone)
            .phone2(phone2)
            .jobs(jobs)
            .departmentID(departmentSeries.getId())
            .entityDisplayName("staffer")
            .departmentHierarchy(hierarchyList)
            .department(departmentSeries.getDepartmentName())
            .positions(positions)
            .bchID(stafferSeries.getBranchId())
            .email(UUID.randomUUID().toString() + "@generate.com")
            .isAdmin(false)
            .build();
    }
}
