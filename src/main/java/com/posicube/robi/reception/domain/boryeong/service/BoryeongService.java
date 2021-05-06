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

        int questionNo1 = 5;
        int questionNo2 = 6;
        int answerNo = 7;

        for (int i=0 ; i < chatBotDF.get(0).length ; i++) {
            log.info(chatBotDF.get(0)[i]);
            if ("대표질문".equals(chatBotDF.get(0)[i])) questionNo1 = i;
            if ("유사질문".equals(chatBotDF.get(0)[i])) questionNo2 = i;
            if ("답변".equals(chatBotDF.get(0)[i])) answerNo = i;
        }
        for (String[] row : chatBotDF) {
            BoryeongDto dto = new BoryeongDto();
            log.info("---------------");
            log.info(row[questionNo1]);
            dto.setQuestion(new ArrayList<>());
            dto.getQuestion().add(row[questionNo1]);
            StringTokenizer stringTokenizer = new StringTokenizer(row[questionNo2], System.lineSeparator());
            while(stringTokenizer.hasMoreTokens()) {
                String question = stringTokenizer.nextToken();
                log.info(question);
                dto.getQuestion().add(question);
            }
            log.info("+++++++++++++++++");
            log.info(row[answerNo]);
            dto.setAnswer(row[answerNo]);
            list.add(dto);
        }
        return list;
    }

}
