package com.yamada.five.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultVO {

    private Integer code;

    private String message;

    private Object data;
}
