package com.posicube.robi.reception.domain.br.department;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentBRRepository extends JpaRepository<DepartmentBR, Long> {

    Optional<DepartmentBR> findDepartmentBRByDepartmentCode(String departmentCode);

    boolean existsByDepartmentName(String departmentName);

    Optional<DepartmentBR> findDepartmentBRByParentCodeAndDepartmentName(String parentCode, String departmentName);

    DepartmentBR findTopByOrderByDepartmentIdDesc();
}
