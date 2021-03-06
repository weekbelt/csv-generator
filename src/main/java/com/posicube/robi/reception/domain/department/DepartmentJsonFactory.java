package com.posicube.robi.reception.domain.department;

import com.fasterxml.jackson.databind.JsonNode;
import com.posicube.robi.reception.domain.department.dto.DepartmentJson;
import com.posicube.robi.reception.domain.department.dto.DepartmentJson.Hierarchy;
import com.posicube.robi.reception.domain.department.dto.DepartmentJson.ParentDept;
import com.posicube.robi.reception.domain.department.dto.DepartmentSeries;
import java.util.List;

public class DepartmentJsonFactory {

    public static DepartmentJson createDepartmentJson(List<Hierarchy> hierarchyList, DepartmentSeries departmentSeries, JsonNode phone, ParentDept parentDept) {
        return DepartmentJson.builder()
            .depth(hierarchyList.size())
            .hierarchy(hierarchyList)
            .deptName(departmentSeries.getDepartmentName())
            .departmentID(departmentSeries.getId())
            .bchID(departmentSeries.getBranchId())
            .phone(phone)
            .parentDept(parentDept)
            .build();
    }
}
