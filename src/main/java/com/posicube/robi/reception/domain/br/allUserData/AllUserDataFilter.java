package com.posicube.robi.reception.domain.br.allUserData;

import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import java.util.Optional;

public class AllUserDataFilter {

    public  static String correctedDepartmentName(String departmentCode, DepartmentBRRepository departmentBRRepository) {
        Optional<DepartmentBR> optionalDepartmentBR = departmentBRRepository.findDepartmentBRByDepartmentCode(departmentCode);
        if (optionalDepartmentBR.isPresent()) {
            DepartmentBR departmentBR = optionalDepartmentBR.get();
            return departmentBR.getDepartmentName();
        }
        return "";
    }

    public static boolean allUserDataFilter(String stafferName) {
        return !stafferName.startsWith("gamsa") && !stafferName.startsWith("감사") && !stafferName.equals("고용복지+센터");
    }

    public static String correctedPhoneType(String phoneNumber) {
        if (phoneNumber.length() == 4) {
            return "내선";
        } else if (phoneNumber.length() == 10) {
            if (phoneNumber.contains("041930")) {
                return "내선";
            } else {
                return "외부국선";
            }
        } else if (phoneNumber.length() == 7) {
            if (phoneNumber.startsWith("930")) {
                return "내선";
            } else {
                return "외부국선";
            }
        }
        return "";
    }

    public static String correctedPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 4) {
            return phoneNumber;
        } else if (phoneNumber.length() == 10) {
            if (phoneNumber.contains("041930")) {
                return phoneNumber.substring(phoneNumber.length() - 4);
            } else {
                return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
            }
        } else if (phoneNumber.length() == 7) {
            if (phoneNumber.startsWith("930")) {
                return phoneNumber.substring(phoneNumber.length() - 4);
            } else {
                return "041-" + phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3);
            }
        }
        return phoneNumber;
    }
}
