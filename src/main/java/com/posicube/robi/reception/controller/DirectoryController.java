package com.posicube.robi.reception.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.delta.DeltaService;
import com.posicube.robi.reception.domain.department.service.DepartmentJsonService;
import com.posicube.robi.reception.domain.job.NewJobJsonService;
import com.posicube.robi.reception.domain.position.NewPositionJsonService;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.domain.staffer.service.NewStafferJsonService;
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
    private final NewStafferJsonService newStafferJsonService;
    private final NewJobJsonService newJobJsonService;
    private final NewPositionJsonService newPositionJsonService;

    @GetMapping("/v1/generate/department")
    @ResponseStatus(HttpStatus.OK)
    public Resource generateDepartment(String branchId) throws CsvValidationException, IOException {
        return departmentJsonService.generateDirectoryDepartment(branchId);
    }

    @GetMapping("/v1/generate/staffer")
    @ResponseStatus(HttpStatus.OK)
    public Resource generateStaffer(String branchId) throws CsvValidationException, IOException {
        return stafferJsonService.generateDirectoryStaffer(branchId);
    }

    @GetMapping("/v1/generate/staffer/delta")
    @ResponseStatus(HttpStatus.OK)
    public List<StafferJson> generateDeltaStaffer() throws IOException {
        return deltaService.generateDelta();
    }

    @GetMapping("/v1/generate/new-staffer")
    public ResponseEntity<Resource> generateNewStaffer() throws IOException {
        Resource newStafferJsonResource = newStafferJsonService.generateNewStaffer();
        return getResponseEntity(newStafferJsonResource, "staffer.json");
    }

    @GetMapping("/v1/generate/new-job")
    public ResponseEntity<Resource> generateNewJob() throws IOException {
        final Resource resource = newJobJsonService.generateNewJob();
        return getResponseEntity(resource, "job.json");
    }

    @GetMapping("/v1/generate/new-position")
    public ResponseEntity<Resource> generateNewPosition() throws IOException {
        final Resource resource = newPositionJsonService.generateNewPosition();
        return getResponseEntity(resource, "position.json");
    }

    private ResponseEntity<Resource> getResponseEntity(Resource newStafferJsonResource, String fileName) throws IOException {
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; fileName=" + fileName)
            .contentLength(newStafferJsonResource.contentLength())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(newStafferJsonResource);
    }
}
