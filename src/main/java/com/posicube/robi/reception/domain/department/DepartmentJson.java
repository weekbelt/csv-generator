package com.posicube.robi.reception.domain.department;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartmentJson {

    private int depth;
    private List<Hierarchy> hierarchy;
    private String deptName;
    private List<TransferableBranch> transferableBranches;
    private Long departmentID;
    private String bchID;
    private JsonNode phone;
    private ParentDept parentDept;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hierarchy implements Comparable {

        private String id;
        private String name;

        @Override
        public int compareTo(Object o) {
            DepartmentJson.Hierarchy hierarchy = (DepartmentJson.Hierarchy) o;
            return Integer.compare(Integer.parseInt(hierarchy.getId()), Integer.parseInt(id));
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransferableBranch {

        private String id;
        private String transferPrefix;
        private String name;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParentDept {

        private String name;
        private Long id;
        private Phone phone;
    }

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
