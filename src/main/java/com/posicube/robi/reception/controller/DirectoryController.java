package com.posicube.robi.reception.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.service.DirectoryGeneratorService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class DirectoryController {

    private final DirectoryGeneratorService directoryGeneratorService;

    @GetMapping("/v1/generate/department")
    @ResponseStatus(HttpStatus.OK)
    public Resource generateDepartment(String branchId) throws CsvValidationException, IOException {
        return directoryGeneratorService.generateDirectoryDepartment(branchId);
    }

    @GetMapping("/v1/generate/staffer")
    @ResponseStatus(HttpStatus.OK)
    public Resource generateStaffer(String branchId) throws CsvValidationException, IOException {
        return directoryGeneratorService.generateDirectoryStaffer(branchId);
    }
}
