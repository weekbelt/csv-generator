package com.posicube.robi.reception.domain.boryeong.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoryeongDto {

    @JsonProperty("LC")
    private String largeCategory;

    @JsonProperty("MC1")
    private String mediumCategory1;

    @JsonProperty("MC2")
    private String mediumCategory2;

    @JsonProperty("SC1")
    private String smallCategory1;

    @JsonProperty("SC2")
    private String smallCategory2;

    @JsonProperty("TitleQuestion")
    private String titleQuestion;

    @JsonProperty("SimilarQuestion")
    private List<String> similarQuestion;

    @JsonProperty("Answer")
    private String answer;

}
