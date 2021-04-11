package com.posicube.robi.reception.domain.staffer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class StafferSeries {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String departmentCode;

    private String departmentName;

    private String positionName;

    private String fullName;

    private String phoneNumber;

    private String jobs;

    private String areas;

    private String branchId;

    public String getFirstName() {
        String lastName = this.fullName.substring(0, 2);
        if (lastName.equals("독고") || lastName.equals("남궁") || lastName.equals("황목") || lastName.equals("선우")
            || lastName.equals("제갈") || lastName.equals("황보")) {
            return this.fullName.substring(2);
        }
        return this.fullName.substring(1);
    }

    public String getLastName() {
        String lastName = this.fullName.substring(0, 2);
        if (lastName.equals("독고") || lastName.equals("남궁") || lastName.equals("황목") || lastName.equals("선우")
            || lastName.equals("제갈") || lastName.equals("황보")) {
            return lastName;
        }
        return this.fullName.substring(0, 1);
    }
}
