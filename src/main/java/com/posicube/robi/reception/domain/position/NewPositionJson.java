package com.posicube.robi.reception.domain.position;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewPositionJson {

    private String id;

    private String name;

    private String branchId;

    private List<String> synonyms;

}
