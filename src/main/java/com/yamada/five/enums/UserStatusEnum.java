package com.yamada.five.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    NORMAL(0, "正常"),
    BANNED(1, "无法接单")
    ;

    private Integer code;

    private String content;
}
