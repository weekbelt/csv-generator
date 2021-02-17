package com.posicube.robi.reception.domain.br.department;

public class DepartmentFilter {

    public static String correctedDepartmentName(String departmentName) {
        if (departmentName.endsWith("읍") || departmentName.endsWith("면")) {
            departmentName += "사무소";
        } else if (departmentName.endsWith("동")) {
            departmentName += "주민센터";
        } else if (departmentName.endsWith("부시장") || departmentName.endsWith("시장")) {
            departmentName += "실";
        }
        return departmentName;
    }
}
