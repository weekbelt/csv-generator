package com.posicube.robi.reception.domain.department.repository;

import com.posicube.robi.reception.domain.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
