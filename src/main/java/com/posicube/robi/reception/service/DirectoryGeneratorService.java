package com.posicube.robi.reception.service;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.util.br.AllUserData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryGeneratorService {

    public void initCorrectedCsv() throws CsvValidationException {
        List<AllUserData> allUserData = AllUserData.init();
        for (AllUserData allUserDatum : allUserData) {
            log.info("stafferId: {} departmentCode: {} stafferName: {} position: {} phoneNumber: {} jobs: {}",
                allUserDatum.getStafferId(), allUserDatum.getDepartmentCode(), allUserDatum.getStafferName(),
                allUserDatum.getPosition(), allUserDatum.getJobs());
        }
    }
}
