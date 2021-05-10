package com.posicube.robi.reception.domain.boryeong.service;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.boryeong.dto.BoryeongDto;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoryeongService {

    private final CsvReaderUtil csvReaderUtil;

    public List<BoryeongDto> convertCsvToJson() throws CsvValidationException {
        List<BoryeongDto> list = new ArrayList<>();
        List<String[]> chatBotDF = csvReaderUtil.convertCsvResourceToDataFrame(new ClassPathResource("csv/br/chatBot/보령시청_챗봇(0507).csv"));

        int largeCategoryNo = 0;
        int mediumCategoryNo1 = 1;
        int mediumCategoryNo2 = 2;
        int smallCategoryNo1 = 3;
        int smallCategoryNo2 = 4;
        int titleQuestionNo = 5;
        int similarQuestionNo = 6;
        int answerNo = 7;

        String largeCategory = "";
        String mediumCategory1 = "";
        String mediumCategory2 = "";
        String smallCategory1 = "";
        String smallCategory2 = "";


        for (String[] row : chatBotDF) {
            BoryeongDto dto = new BoryeongDto();

            largeCategory = checkNext(row[largeCategoryNo], largeCategory);
            dto.setLargeCategory(largeCategory.trim());
            mediumCategory1 = checkNext(row[mediumCategoryNo1], mediumCategory1);
            dto.setMediumCategory1(mediumCategory1.trim());
            mediumCategory2 = checkNext(row[mediumCategoryNo2], mediumCategory2);
            dto.setMediumCategory2(mediumCategory2.trim());
            smallCategory1 = checkNext(row[smallCategoryNo1], smallCategory1);
            dto.setSmallCategory1(smallCategory1.trim());
            smallCategory2 = checkNext(row[smallCategoryNo2], smallCategory2);
            dto.setSmallCategory2(smallCategory2.trim());
            dto.setTitleQuestion(row[titleQuestionNo]);

            dto.setSimilarQuestion(new ArrayList<>());
            StringTokenizer stringTokenizer = new StringTokenizer(row[similarQuestionNo], System.lineSeparator());
            while(stringTokenizer.hasMoreTokens()) {
                String question = stringTokenizer.nextToken();
                dto.getSimilarQuestion().add(question.trim());
            }
            dto.setAnswer(row[answerNo].trim());
            list.add(dto);
        }
        return list;
    }

    private String checkNext(String row, String largeCategory) {
        if ("".equals(row)) {
            return largeCategory;
        } else {
            return row;
        }
    }

}
