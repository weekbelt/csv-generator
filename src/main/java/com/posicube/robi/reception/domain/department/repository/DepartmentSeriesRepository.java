package com.posicube.robi.reception.domain.department.repository;

import com.posicube.robi.reception.domain.department.DepartmentSeries;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentSeriesRepository extends JpaRepository<DepartmentSeries, Long> {

    Optional<DepartmentSeries> findByDepartmentCode(String parentDepartmentCode);
}
