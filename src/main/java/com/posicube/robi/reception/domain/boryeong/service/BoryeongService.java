package com.posicube.robi.reception.domain.boryeong.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.boryeong.dto.BoryeongDto;
import com.posicube.robi.reception.util.CsvReaderUtil;
import com.posicube.robi.reception.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoryeongService {

    private final CsvReaderUtil csvReaderUtil;

    public List<BoryeongDto> convertCsvToJson() throws CsvValidationException {
        List<BoryeongDto> list = new ArrayList<>();
        List<String[]> chatBotDF = csvReaderUtil.convertCsvResourceToDataFrame(new ClassPathResource("csv/br/chatBot/chatBot.csv"));

        for (String[] st : chatBotDF) {
            BoryeongDto dto = new BoryeongDto();
            log.info("---------------");
            log.info(st[5]);
            dto.setQuestion(new ArrayList<>());
            dto.getQuestion().add(st[5]);
            StringTokenizer stringTokenizer = new StringTokenizer(st[6], System.lineSeparator());
            while(stringTokenizer.hasMoreTokens()) {
                String question = stringTokenizer.nextToken();
                log.info(question);
                dto.getQuestion().add(question);
            }
            log.info("+++++++++++++++++");
            log.info(st[7]);
            dto.setAnswer(st[7]);
            list.add(dto);
        }
        return list;
    }

}
