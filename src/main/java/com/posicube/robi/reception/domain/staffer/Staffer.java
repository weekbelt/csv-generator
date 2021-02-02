package com.posicube.robi.reception.domain.staffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.BaseTimeEntity;
import com.posicube.robi.reception.domain.PhoneType;
import com.posicube.robi.reception.domain.department.Department;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
@Entity
public class Staffer extends BaseTimeEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String fullName;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PhoneType phoneType;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private Long deptId;            // JSON에 정의 된 값 그대로 Insert, deptId 가 존재하지 않는 Case가 있다.

    @Column
    private String deptName;

    @Column(nullable = false)
    private boolean isAdmin;

    @Column(nullable = false)
    private String branchId;

    @Column
    private String tasks;           // , Separator 로 List 형태로 들어감

    @Column
    private String title;           // , Separator 로 List 형태로 들어감

    @Column(columnDefinition = "json")
    @Convert(converter = StafferJsonConverter.class)
    private StafferJson properties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Department.id")
    private Department department;

    public static class StafferJsonConverter implements AttributeConverter<StafferJson, String> {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(StafferJson myDoc) {
            try {
                return objectMapper.writeValueAsString(myDoc);
            } catch (final JsonProcessingException e) {
                log.error("JsonProcessingException", e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public StafferJson convertToEntityAttribute(String myDocJson) {
            try {
                String converted = JsonUtil.jsonStringToObject(myDocJson);
                return objectMapper.readValue(converted, StafferJson.class);
            } catch (final IOException e) {
                log.error("IOException", e);
                throw new RuntimeException(e);
            }
        }

    }

}
