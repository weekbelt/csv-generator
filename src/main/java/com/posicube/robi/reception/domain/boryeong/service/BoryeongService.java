package com.posicube.robi.reception.domain.boryeong.service;

import com.opencsv.exceptions.CsvValidationException;
import com.posicube.robi.reception.domain.boryeong.dto.BoryeongDto;
import com.posicube.robi.reception.util.CsvReaderUtil;
import java.util.ArrayList;
import java.util.HashMap;
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
        HashMap<String, String> checkDuple = new HashMap<>();
        List<BoryeongDto> list = new ArrayList<>();
        List<String[]> chatBotDF = csvReaderUtil.convertCsvResourceToDataFrame(new ClassPathResource("csv/br/chatBot/보령시청_챗봇(0520).csv"));

        int largeCategoryNo = 0;
        int mediumCategoryNo = 1;
        int smallCategoryNo = 2;
        int questionNameNo = 3;
        int titleQuestionNo = 4;
        int similarQuestionNo = 5;
        int answerNo = 6;
        int useFlag = 8;

        final String imageUrl = "https://chatbot1.blob.core.windows.net/%24web/";

        String largeCategory = "";
        String mediumCategory = "";
        String smallCategory = "";
        String questionName = "";

        for (String[] row : chatBotDF) {

            BoryeongDto dto = new BoryeongDto();
            largeCategory = checkNext(row[largeCategoryNo], largeCategory);
            dto.setLargeCategory(largeCategory.trim());
            mediumCategory = checkNext(row[mediumCategoryNo], mediumCategory);
            dto.setMediumCategory(mediumCategory.trim());
            smallCategory = checkNext(row[smallCategoryNo], smallCategory);
            dto.setSmallCategory(smallCategory.trim());
            questionName = checkNext(row[questionNameNo], questionName);
            dto.setQuestionName(questionName.trim());
            dto.setTitleQuestion(row[titleQuestionNo].trim());

            if (!"o".equals(row[useFlag])) continue;
            dto.setSimilarQuestion(new ArrayList<>());

            if (checkDuple(checkDuple, row[titleQuestionNo])) {
                log.error(row[titleQuestionNo]);
                throw new RuntimeException("Question Duplication Error");
            }
            else {
                checkDuple.put(row[titleQuestionNo], "");
            }

            StringTokenizer stringTokenizer = new StringTokenizer(row[similarQuestionNo], System.lineSeparator());
            while (stringTokenizer.hasMoreTokens()) {
                String question = stringTokenizer.nextToken().trim();
                if (checkDuple(checkDuple, question)) {
                    //log.error(question);
                    continue;
                }
                checkDuple.put(question, "");
                dto.getSimilarQuestion().add(question.trim());
            }

            stringTokenizer = new StringTokenizer(row[answerNo], System.lineSeparator());
            StringBuilder builder = new StringBuilder();

            boolean stringflag = false;
            while(stringTokenizer.hasMoreTokens()) {
                String tokenString = stringTokenizer.nextToken().trim();

                if ("<IMAGE>".equals(tokenString)) {
                    if (stringflag) {
                        builder.append(System.lineSeparator());
                        stringflag = false;
                    }
                    builder.append(tokenString).append(System.lineSeparator());
                    tokenString = stringTokenizer.nextToken().trim();
                    builder.append(tokenString).append(System.lineSeparator());
                    tokenString = stringTokenizer.nextToken().trim();
                    if (!tokenString.startsWith("http")) {
                        if (!tokenString.endsWith(".png")) {
                            log.error("not image :{}", tokenString);
                        }
                        builder.append(imageUrl).append(tokenString).append(System.lineSeparator()).append(System.lineSeparator());
                    } else {
                        builder.append(tokenString).append(System.lineSeparator()).append(System.lineSeparator());
                    }
                } else if ("<LINK>".equals(tokenString)) {
                    if (stringflag) {
                        builder.append(System.lineSeparator());
                        stringflag = false;
                    }
                    builder.append(tokenString).append(System.lineSeparator());
                    tokenString = stringTokenizer.nextToken().trim();
                    builder.append(tokenString).append(System.lineSeparator());
                    tokenString = stringTokenizer.nextToken().trim();
                    builder.append(tokenString).append(System.lineSeparator());
                    tokenString = stringTokenizer.nextToken().trim();
                    builder.append(tokenString).append(System.lineSeparator()).append(System.lineSeparator());
                } else {
                    stringflag = true;
                    builder.append(tokenString).append(System.lineSeparator());
                }

            }
            dto.setAnswer(builder.toString().trim());

            list.add(dto);
        }
        return list;
    }

    private boolean checkDuple(HashMap<String, String> checkDuple, String key) {
        return checkDuple.containsKey(key);
    }

    private String checkNext(String row, String largeCategory) {
        if ("".equals(row)) {
            return largeCategory;
        } else {
            return row;
        }
    }

}
