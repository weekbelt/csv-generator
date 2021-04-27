package com.posicube.robi.reception.domain.staffer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewStafferJson {

    private String id;

    private String name;

    private Phone phone;

    private Phone phone2;

    private String departmentId;

    private String departmentName;

    private List<String> jobs;

    private List<String> positions;

    private List<String> areas;

    private String branchId;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Phone {

        private String type;
        private String number;
    }
}
