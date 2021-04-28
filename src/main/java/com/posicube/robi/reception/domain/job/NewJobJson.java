package com.posicube.robi.reception.domain.job;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewJobJson {

    private String id;

    private String name;

    private Integer depth;

    private String branchId;

    private String jobType;

    private List<String> synonyms;
}
