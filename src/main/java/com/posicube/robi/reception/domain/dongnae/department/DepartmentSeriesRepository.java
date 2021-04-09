package com.posicube.robi.reception.domain.dongnae.department;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentSeriesRepository extends JpaRepository<DepartmentSeries, Long> {

    Optional<DepartmentSeries> findByDepartmentCode(String parentDepartmentCode);
}
