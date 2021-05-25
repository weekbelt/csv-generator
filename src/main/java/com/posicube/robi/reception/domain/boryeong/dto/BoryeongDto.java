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

    @JsonProperty("MC")
    private String mediumCategory;

    @JsonProperty("SC")
    private String smallCategory;

    @JsonProperty("QuestionName")
    private String questionName;

    @JsonProperty("TitleQuestion")
    private String titleQuestion;

    @JsonProperty("SimilarQuestion")
    private List<String> similarQuestion;

    @JsonProperty("Answer")
    private String answer;

}
