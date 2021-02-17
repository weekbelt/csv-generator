package com.posicube.robi.reception.domain.br;

import com.posicube.robi.reception.domain.br.allUserData.AllUserData;
import com.posicube.robi.reception.domain.br.department.DepartmentJson.Hierarchy;
import com.posicube.robi.reception.domain.br.phoneBook.PhoneBook;
import com.posicube.robi.reception.domain.br.staffer.BrStaffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BRRepository {

    public static Set<AllUserData> allUserDataSet = new HashSet<>();

    public static Set<PhoneBook> phoneBookSet = new HashSet<>();

    public static Set<BrStaffer> brStafferSet = new HashSet<>();

    public static Map<Long, List<Hierarchy>> hierarchyMap = new HashMap<>();
}
