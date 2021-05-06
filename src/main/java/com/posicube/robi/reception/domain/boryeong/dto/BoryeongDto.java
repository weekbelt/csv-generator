package com.posicube.robi.reception.domain.boryeong.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoryeongDto {

    @JsonProperty("Question")
    private List<String> question;

    @JsonProperty("Answer")
    private String answer;

}
