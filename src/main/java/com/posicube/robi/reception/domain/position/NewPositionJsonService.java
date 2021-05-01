package com.posicube.robi.reception.domain.position;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Service
public class NewPositionJsonService {

    private final ObjectMapper objectMapper;
    private final JsonUtil jsonUtil;
    private final PositionRepository positionRepository;

    public Resource generateNewPosition(String branchName) throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<StafferJson> stafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/" + branchName + "/old/staffer.json"), new TypeReference<>() {
        });

        savePositions(stafferJsonList);
        List<NewPositionJson> newPositionJsonList = getNewPositionJsonList();

        return jsonUtil.getByteArrayResource(newPositionJsonList, objectMapper);
    }

    private void savePositions(List<StafferJson> stafferJsonList) {
        stafferJsonList.forEach(stafferJson -> {
            stafferJson.getPositions().forEach(positionName -> {
                saveIfNotExists(stafferJson, positionName);
            });
        });
    }

    private void saveIfNotExists(StafferJson stafferJson, String positionName) {
        String trimPosition = positionName.trim();
        if (!positionRepository.existsByName(trimPosition) && StringUtils.isNotBlank(trimPosition)) {
            Position position = PositionConverter.convertToPosition(stafferJson, trimPosition);
            positionRepository.save(position);
        }
    }

    private List<NewPositionJson> getNewPositionJsonList() {
        List<Position> positionList = positionRepository.findAll();
        return positionList.stream().map(PositionConverter::convertToNewPositionJsonFromPosition).collect(Collectors.toList());
    }
}
