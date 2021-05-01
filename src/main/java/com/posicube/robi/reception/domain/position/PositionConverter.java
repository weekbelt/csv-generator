package com.posicube.robi.reception.domain.position;

import com.posicube.robi.reception.domain.staffer.StafferJson;
import java.util.ArrayList;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionConverter {

    public static Position convertToPosition(StafferJson stafferJson, String positionName) {
        return Position.builder()
            .id(UUID.randomUUID().toString())
            .name(positionName)
            .branchId(stafferJson.getBchID())
            .build();
    }

    public static NewPositionJson convertToNewPositionJsonFromPosition(Position position) {
        return NewPositionJson.builder()
            .id(position.getId())
            .name(position.getName().trim())
            .branchId(position.getBranchId())
            .synonyms(new ArrayList<>())
            .build();
    }
}
