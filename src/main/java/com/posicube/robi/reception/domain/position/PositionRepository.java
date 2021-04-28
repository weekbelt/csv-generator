package com.posicube.robi.reception.domain.position;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, String> {

    boolean existsByName(String name);
}
