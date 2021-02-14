package com.posicube.robi.reception.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.exception.CsvFileHandlingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@NoArgsConstructor
public class CsvReaderUtil {

    public List<String[]> convertCsvResourceToDataFrame(Resource resource) throws CsvValidationException {
        List<String[]> series = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(resource.getFile()))) {
            csvReader.readNext();      // column 명 삭제

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                series.add(nextLine);
            }
        } catch (IOException e) {
            throw new CsvFileHandlingException("Csv file reading failed!!", e);
        }

        return series;
    }

}
