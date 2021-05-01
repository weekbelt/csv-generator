package com.posicube.robi.reception.domain.department.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentId {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long oldDepartmentId;

    private String newDepartmentId;

}
