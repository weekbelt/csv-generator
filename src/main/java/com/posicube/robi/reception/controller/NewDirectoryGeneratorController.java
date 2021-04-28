package com.posicube.robi.reception.controller;


import com.posicube.robi.reception.domain.department.service.NewDepartmentJsonService;
import com.posicube.robi.reception.domain.job.NewJobJsonService;
import com.posicube.robi.reception.domain.position.NewPositionJsonService;
import com.posicube.robi.reception.domain.staffer.service.NewStafferJsonService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class NewDirectoryGeneratorController {

    private final NewDepartmentJsonService newDepartmentJsonService;
    private final NewStafferJsonService newStafferJsonService;
    private final NewJobJsonService newJobJsonService;
    private final NewPositionJsonService newPositionJsonService;

    @GetMapping("/v1/generate/new-department")
    public ResponseEntity<Resource> generateNewDepartment() throws IOException {
        Resource newStafferJsonResource = newDepartmentJsonService.generateNewDepartment();
        return getResponseEntity(newStafferJsonResource, "department.json");
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
