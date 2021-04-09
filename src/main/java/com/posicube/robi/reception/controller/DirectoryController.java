package com.posicube.robi.reception.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.br.department.DepartmentJson;
import com.posicube.robi.reception.service.DirectoryGeneratorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public List<DepartmentJson> generateDepartment(String branchId) throws CsvValidationException {
        return directoryGeneratorService.generateDirectoryDepartment(branchId);
    }

    @GetMapping("/v1/generate/staffer")
    @ResponseStatus(HttpStatus.OK)
    public void generateStaffer() {
        directoryGeneratorService.generateDirectoryStaffer();
    }
}
