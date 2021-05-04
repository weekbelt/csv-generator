package com.posicube.robi.reception.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.boryeong.dto.BoryeongDto;
import com.posicube.robi.reception.domain.boryeong.service.BoryeongService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BoryeongController {

    private final BoryeongService boryeongService;

    @GetMapping("/v1/generate/brchatbot")
    @ResponseStatus(HttpStatus.OK)
    public List<BoryeongDto> generateChatBot() throws CsvValidationException {
        return boryeongService.convertCsvToJson();
    }

    @GetMapping("/v1/generate/brchatbot/json")
    @ResponseStatus(HttpStatus.OK)
    public List<BoryeongDto> generateChatBotJson() throws CsvValidationException {
        return boryeongService.convertCsvToJson();
    }
}
