package com.posicube.robi.reception.domain.br.phoneBook;

import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class PhoneBookFilter {

    public static String correctLastName(String lastName, String housePhone) {
        if (lastName.contains("fax") || lastName.contains("FAX") || lastName.contains("FXA") || housePhone
            .contains("FAX") || lastName.contains("fxa")) {
            return "팩스";
        } else if (lastName.endsWith("팀장")) {
            return lastName.substring(0, lastName.length() - 2).trim();
        }
        return lastName;
    }

    public static String correctedDepartmentName(String company, String housePhone) {
        if (housePhone.equals("의장") || housePhone.equals("부의장") || housePhone.equals("의원") || housePhone.endsWith("위원장")
            || housePhone.equals("선장") || StringUtils.isBlank(housePhone)) {
            return company;
        } else if (housePhone.equals("부시장")) {
            return "부시장실";
        } else if (housePhone.equals("시장") | housePhone.equals("비서실장") || housePhone.equals("민원보좌관") || housePhone
            .equals("수행비서")) {
            return "시장실";
        } else if (housePhone.equals("농업기술센터소장") || housePhone.equals("보건소장실")) {
            return housePhone.substring(0, housePhone.length() - 2);
        } else if (housePhone.endsWith("동장")) {
            return housePhone.substring(0, housePhone.length() - 1) + "주민센터";
        } else if (housePhone.endsWith("면장") || housePhone.endsWith("읍장")) {
            return housePhone.substring(0, housePhone.length() - 1) + "사무소";
        } else if (housePhone.endsWith("장")) {
            return housePhone.substring(0, housePhone.length() - 1);
        } else if (housePhone.equals("공중보건의사")) {
            return "진료팀";
        } else if (housePhone.equals("전문위원")) {
            return "의회사무국";
        } else if (housePhone.equals("대천2동")) {
            return "대천2동주민센터";
        }
        return housePhone;
    }

    public static boolean isUnique(Set<PhoneBook> phoneBookSet, PhoneBook phoneBook) {
        boolean isUnique = true;
        for (PhoneBook element : phoneBookSet) {
            if (element.getLastName().equals(phoneBook.getLastName()) &&
                element.getCompany().equals(phoneBook.getCompany())) {
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }
}
