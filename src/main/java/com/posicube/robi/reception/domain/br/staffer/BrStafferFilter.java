package com.posicube.robi.reception.domain.br.staffer;

import com.posicube.robi.reception.domain.br.BRRepository;
import com.posicube.robi.reception.domain.br.allUserData.AllUserData;
import com.posicube.robi.reception.domain.br.department.DepartmentBR;
import com.posicube.robi.reception.domain.br.department.DepartmentBRRepository;
import com.posicube.robi.reception.domain.br.department.DepartmentJson.Hierarchy;
import com.posicube.robi.reception.domain.br.phoneBook.PhoneBook;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class BrStafferFilter {

    public static String correctedFirstName(String name) {
        if (name.equals("팩스")) {
            return "";
        } else if (name.startsWith("남궁") || name.startsWith("독고") || name.startsWith("선우") ||
            name.startsWith("황보") || name.startsWith("황목")) {
            return name.substring(2);
        }

        return name.substring(1);
    }

    public static String correctedLastName(String name) {
        if (name.equals("팩스")) {
            return name;
        } else if (name.startsWith("남궁") || name.startsWith("독고") || name.startsWith("선우") ||
            name.startsWith("황보") || name.startsWith("황목")) {
            return name.substring(0, 2);
        }
        return name.substring(0, 1);
    }

    public static String correctedTitle(String position, String housePhone) {
        StringBuilder title = new StringBuilder();

        if (position.equals("시장") || position.equals("부시장") || position.equals("선장")) {
            return position;
        } else if (position.equals("과장") || position.equals("단장") || position.equals("국장") || position.equals("팀장")) {
            List<String> positions = new ArrayList<>();
            positions.add(position);
            positions.add(housePhone + "장");

            return makeTitle(title, positions);
        } else if (position.equals("동장")) {
            List<String> positions = new ArrayList<>();
            positions.add(position);
            positions.add(housePhone.substring(0, 4) + "장");

            return makeTitle(title, positions);
        } else if (position.equals("면장") || position.equals("읍장")) {
            List<String> positions = new ArrayList<>();
            positions.add(position);
            positions.add(housePhone.substring(0, 3) + "장");

            return makeTitle(title, positions);
        }
        return "";
    }

    public static String makeTitle(StringBuilder title, List<String> positions) {
        for (int i = 0; i < positions.size(); i++) {
            if (i != positions.size() - 1) {
                title.append(positions.get(i)).append(",");
            } else {
                title.append(positions.get(i));
            }
        }
        return title.toString();
    }

    public static String[] getPhoneInfo(String lastName, String phoneType, String phoneNumber, String station) {
        String[] phoneInfo = new String[3];
        if (lastName.equals("팩스")) {
            phoneInfo[0] = "041-" + station;
            phoneInfo[1] = "2";
            phoneInfo[2] = "외부국선";
        } else if (StringUtils.isBlank(phoneType) && StringUtils.isBlank(phoneNumber) && StringUtils
            .isNotBlank(station)) {
            if (station.equals("의사")) {
                phoneInfo[0] = "";
                phoneInfo[1] = "";
                phoneInfo[2] = "";
            } else if (station.length() != 4 && station.contains("930")) {
                phoneInfo[0] = station.substring(station.length() - 4);
                phoneInfo[1] = "1";
                phoneInfo[2] = "내선";
            } else if (station.length() != 4 && !station.contains("930")) {
                phoneInfo[0] = "041-" + station;
                phoneInfo[1] = "2";
                phoneInfo[2] = "외부국선";
            } else if (station.length() == 4) {
                phoneInfo[0] = station;
                phoneInfo[1] = "1";
                phoneInfo[2] = "내선";
            }
        } else if (StringUtils.isBlank(phoneType) && StringUtils.isBlank(phoneNumber) && StringUtils.isBlank(station)) {
            phoneInfo[0] = "";
            phoneInfo[1] = "";
            phoneInfo[2] = "";
        } else {
            phoneInfo[0] = phoneNumber;
            if (phoneType.equals("내선")) {
                phoneInfo[1] = "1";
            } else if (phoneType.equals("외부국선")) {
                phoneInfo[1] = "2";
            }
            phoneInfo[2] = phoneType;
        }
        return phoneInfo;
    }

    public static String correctedEmail(String personalId) {
        return personalId + "@generated.email";
    }

    public static DepartmentBR getDepartmentBR(String allUserDepartmentName, String allUserDepartmentCode, String allUSerDepartmentId, String phoneBookHousePhone, DepartmentBRRepository departmentBRRepository) {
        // departmentName과 housePhone이 일치하지 않다면 housePhone이 departmentName의 하부 부서라는 이야기
        if (!allUserDepartmentName.equals(phoneBookHousePhone)) {
            // 하부 부서가 이미 저장되어있는지 확인하는 로직
            Optional<DepartmentBR> department = departmentBRRepository.findDepartmentBRByParentCodeAndDepartmentName(allUserDepartmentCode, phoneBookHousePhone);
            if (department.isPresent()) {       // 존재한다면 찾아서 반환
                return department.get();
            } else {                            // 존재하지 않는다면 새로 생성 후 반환
                DepartmentBR departmentBR = departmentBRRepository.findTopByOrderByDepartmentIdDesc();
                Long newDepartmentId = departmentBR.getDepartmentId() + 1;
                String newDepartmentCode = departmentBR.getDepartmentCode() + "." + newDepartmentId;

                // hierarchy에 저장
                Map<Long, List<Hierarchy>> hierarchyMap = BRRepository.hierarchyMap;
                List<Hierarchy> hierarchyList = hierarchyMap.get(Long.valueOf(allUSerDepartmentId));
                hierarchyList.add(Hierarchy.builder()
                    .id(allUSerDepartmentId)
                    .name(allUserDepartmentName)
                    .build());
                hierarchyMap.put(newDepartmentId, hierarchyList);

                // 영속성컨텍스트에 저장
                return departmentBRRepository.save(DepartmentBR.builder()
                    .departmentId(newDepartmentId)
                    .departmentCode(newDepartmentCode)
                    .departmentName(phoneBookHousePhone)
                    .parentCode(allUserDepartmentCode)
                    .build());
            }

        } else {
            return departmentBRRepository.findById(Long.valueOf(allUSerDepartmentId))
                .orElseThrow(() -> new IllegalArgumentException("해당하는 부서가 존재하지 않습니다. departmentId: " + allUSerDepartmentId));
        }
    }

    public static String getAdmin(String lastName) {
        if (lastName.equals("정용일")) {
            return "1";
        }
        return "0";
    }

    public static String correctedJobs(String jobs) {
//        List<String> jobList = new ArrayList<>();
//        for (String job : jobs.split("[,.○]")) {
//            job = job.trim();
//            if (job.contains("@")) {
//                String[] split = job.split("[@]");
//                if (split.length >= 2) {
//                    jobList.add(split[1]);
//                }
//            } else {
//                jobList.add(job);
//            }
//        }
//
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < jobList.size(); i++) {
//            if (i != jobList.size() - 1) {
//                stringBuilder.append(jobList.get(i)).append(",");
//            } else {
//                stringBuilder.append(jobList.get(i));
//            }
//        }
//        jobs = stringBuilder.toString();

        List<String> jobList = new ArrayList<>();
        String[] splitJobs = jobs.split("ㆍ-/,·\\(\\)@");
        for (String splitJob : splitJobs) {
            if (splitJob.length() != 1 && !isInAccuracy(splitJob)) {
                jobList.add(splitJob);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jobList.size(); i++) {
            if (i != jobList.size() - 1) {
                stringBuilder.append(jobList.get(i)).append(",");
            } else {
                stringBuilder.append(jobList.get(i));
            }
        }
        return stringBuilder.toString();
    }

    private static boolean isInAccuracy(String splitJob) {
        return splitJob.equals("업무") || splitJob.equals("수립") || splitJob.equals("운영") || splitJob.equals("관리") ||
            splitJob.equals("보조") || splitJob.equals("연계") || splitJob.equals("운행") || splitJob.equals("운용") ||
            splitJob.equals("지원") || splitJob.equals("추진") || splitJob.equals("협력") || splitJob.equals("관한") ||
            splitJob.equals("사항") || splitJob.equals("총괄");
    }

    public static AllUserData getAllUserData(PhoneBook phoneBook, Set<AllUserData> allUserDataSet) {
        for (AllUserData allUserData : allUserDataSet) {
            if (allUserData.getDepartmentName().equals(phoneBook.getCompany()) &&
                allUserData.getStafferName().equals(phoneBook.getLastName())) {
                return allUserData;
            }
        }
        return AllUserData.builder().build();
    }

}
