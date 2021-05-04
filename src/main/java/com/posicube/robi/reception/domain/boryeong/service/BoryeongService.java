package com.posicube.robi.reception.domain.boryeong.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.boryeong.dto.BoryeongDto;
import com.posicube.robi.reception.util.CsvReaderUtil;
import com.posicube.robi.reception.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoryeongService {

    private final ObjectMapper objectMapper;
    private final CsvReaderUtil csvReaderUtil;
    private final JsonUtil jsonUtil;

    public List<BoryeongDto> convertCsvToJson() throws CsvValidationException {
        List<BoryeongDto> list = new ArrayList<>();
        List<String[]> chatBotDF = csvReaderUtil.convertCsvResourceToDataFrame(new ClassPathResource("csv/br/chatBot/chatBot.csv"));

        chatBotDF.forEach(chatBot -> {
            log.info(chatBot.toString());
        });



        return list;
    }
}
