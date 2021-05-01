package com.posicube.robi.reception.domain.position;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.staffer.StafferJson;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@RequiredArgsConstructor
@Service
public class NewPositionJsonService {

    private final ObjectMapper objectMapper;
    private final JsonUtil jsonUtil;
    private final PositionRepository positionRepository;

    public Resource generateNewPosition() throws IOException {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<StafferJson> stafferJsonList = objectMapper.readValue(ResourceUtils.getFile("classpath:json/br/past/staffer.json"), new TypeReference<>() {
        });

        savePositions(stafferJsonList);
        List<Position> positionList = positionRepository.findAll();
        final List<NewPositionJson> newPositionJsonList = positionList.stream().map(position -> NewPositionJson.builder()
            .id(position.getId())
            .name(position.getName().trim())
            .branchId(position.getBranchId())
            .synonyms(new ArrayList<>())
            .build()
        ).collect(Collectors.toList());

        return jsonUtil.getByteArrayResource(newPositionJsonList, objectMapper);
    }

    private void savePositions(List<StafferJson> stafferJsonList) {
        stafferJsonList.forEach(stafferJson -> {
            final List<String> positions = stafferJson.getPositions();
            positions.forEach(positionName -> {
                if (!positionRepository.existsByName(positionName)) {
                    final Position position = Position.builder()
                        .id(UUID.randomUUID().toString())
                        .name(positionName)
                        .branchId(stafferJson.getBchID())
                        .build();
                    positionRepository.save(position);
                }
            });
        });
    }
}
