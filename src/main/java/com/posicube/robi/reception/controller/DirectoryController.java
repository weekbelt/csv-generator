package com.posicube.robi.reception.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.delta.DeltaService;
import com.posicube.robi.reception.domain.department.service.DepartmentJsonService;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.domain.staffer.service.StafferJsonService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class DirectoryController {

    private final DepartmentJsonService departmentJsonService;
    private final StafferJsonService stafferJsonService;
    private final DeltaService deltaService;

    @GetMapping("/v1/generate/department")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> generateDepartment(String branchId) throws CsvValidationException, IOException {
        Resource resource = departmentJsonService.generateDirectoryDepartment(branchId);
        return getResourceResponseEntity(resource, "department.json");
    }

    @GetMapping("/v1/generate/staffer")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> generateStaffer(String branchId) throws CsvValidationException, IOException {
        Resource resource = stafferJsonService.generateDirectoryStaffer(branchId);
        return getResourceResponseEntity(resource, "staffer.json");
    }

    @GetMapping("/v1/generate/staffer/delta")
    @ResponseStatus(HttpStatus.OK)
    public List<StafferJson> generateDeltaStaffer() throws IOException {
        return deltaService.generateDelta();
    }

    private ResponseEntity<Resource> getResourceResponseEntity(Resource departmentJsonResource, String fileName) throws IOException {
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; fileName=" + fileName)
            .contentLength(departmentJsonResource.contentLength())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(departmentJsonResource);
    }
}
