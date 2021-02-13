package com.posicube.robi.reception.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.service.DirectoryGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class DirectoryController {

    private final DirectoryGeneratorService directoryGeneratorService;

    @GetMapping("/directory/init/correctedData")
    public void initDirectoryCsv() throws CsvValidationException {
        directoryGeneratorService.initCorrectedCsv();
    }


}
