package com.posicube.robi.reception.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {

    boolean existsByName(String name);
}
