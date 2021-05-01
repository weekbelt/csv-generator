package com.posicube.robi.reception.domain.department.repository;

import com.posicube.robi.reception.domain.department.dto.DepartmentId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentIdRepository extends JpaRepository<DepartmentId, Long> {

    Optional<DepartmentId> findByOldDepartmentId(Long oldDepartmentId);
}
