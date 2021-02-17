package com.posicube.robi.reception.domain.br.department;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class DepartmentBR {

    @Id
    private Long departmentId;

    @Lob
    private String departmentCode;

    private String parentCode;

    private String departmentName;

}
