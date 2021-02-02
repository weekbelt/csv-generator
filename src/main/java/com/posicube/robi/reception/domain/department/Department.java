package com.posicube.robi.reception.domain.department;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posicube.robi.reception.domain.BaseTimeEntity;
import com.posicube.robi.reception.domain.PhoneType;
import com.posicube.robi.reception.domain.staffer.Staffer;
import com.posicube.robi.reception.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
public class Department extends BaseTimeEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PhoneType phoneType;

    @Column(nullable = false)
    private String branchId;

    @Column(columnDefinition = "json")
    @Convert(converter = DepartmentJsonConverter.class)
    private DepartmentJson properties;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Staffer> staffers;

    public static class DepartmentJsonConverter implements AttributeConverter<DepartmentJson, String> {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(DepartmentJson myDoc) {
            try {
                return objectMapper.writeValueAsString(myDoc);
            } catch (final JsonProcessingException e) {
                log.error("JsonProcessingException", e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public DepartmentJson convertToEntityAttribute(String myDocJson) {
            try {
                String converted = JsonUtil.jsonStringToObject(myDocJson);
                return objectMapper.readValue(converted, DepartmentJson.class);
            } catch (final IOException e) {
                log.error("IOException", e);
                throw new RuntimeException(e);
            }
        }

    }
}
