package com.yamada.five.exception;

import com.yamada.five.enums.ResultEnums;
import lombok.Getter;

import java.util.Map;

@Getter
public class FiveApiException extends RuntimeException {

    private Integer code;

    private Map<String, String> data;

    public FiveApiException(ResultEnums enums) {
        super(enums.getMsg());
        this.code = enums.getCode();
    }

    public FiveApiException(ResultEnums enums, Map<String, String> data) {
        super(enums.getMsg());
        this.code = enums.getCode();
        this.data = data;
    }
}
