package com.yamada.five.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AreaEnum {

    EASE(0, "东区"),
    WEST(1, "西区")
    ;

    private Integer code;

    private String area;
}
