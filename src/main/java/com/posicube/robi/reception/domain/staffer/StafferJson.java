package com.posicube.robi.reception.domain.staffer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class StafferJson {

    private long staffID;
    private String firstName;
    private String lastName;
    private String fullName;
    private Phone phone;
    private JsonNode phone2;
    private List<String> jobs;
    private long departmentID;
    private String entityDisplayName;
    private List<Branch> transferableBranches;
    private List<Department> departmentHierarchy;
    private String department;
    private String email;
    private List<String> positions;
    private String bchID;
    private boolean isAdmin;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Phone {

        @JsonProperty("type")
        private String type;
        @JsonProperty("number")
        private String number;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Branch {

        private String transferPrefix;
        private String id;
        private String name;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Department {

        private String id;
        private String name;
    }
}
