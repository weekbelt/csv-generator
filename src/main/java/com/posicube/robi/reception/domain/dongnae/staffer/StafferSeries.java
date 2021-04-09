package com.posicube.robi.reception.domain.dongnae.staffer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class StafferSeries {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
