package com.posicube.robi.reception.domain.department;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DepartmentSeries {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departmentCode;

    private String departmentName;

    private String parentDepartmentCode;

    private String departmentPhoneNumber;

    private String branchId;
}
