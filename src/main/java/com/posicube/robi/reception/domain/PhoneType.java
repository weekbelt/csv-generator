package com.posicube.robi.reception.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PhoneType {
    INWARD_DIALING,     // 내선
    OUTWARD_DIALING,    // 외선
    GROUP_DIALING,      // 그룹
    NONE
}
