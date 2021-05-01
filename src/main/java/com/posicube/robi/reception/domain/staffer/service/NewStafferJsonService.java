package com.posicube.robi.reception.domain.staffer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.NewStafferJson;
import com.posicube.robi.reception.domain.staffer.StafferConverter;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Transactional
@Service
public class NewStafferJsonService {

    private final ObjectMapper objectMapper;
    private final JsonUtil jsonUtil;

    public Resource generateNewStaffer(String branchName) throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<StafferJson> stafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/" + branchName + "/old/staffer.json"), new TypeReference<>() {
        });

        List<NewStafferJson> newStafferJsonList = stafferJsonList.stream().map(StafferConverter::convertToNewStaffer).collect(Collectors.toList());

        return jsonUtil.getByteArrayResource(newStafferJsonList, objectMapper);
    }
}
