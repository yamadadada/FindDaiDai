package com.yamada.five.exception;

import com.yamada.five.enums.ResultEnums;
import lombok.Getter;

import java.util.Map;

@Getter
public class FiveException extends RuntimeException {

    private Integer code;

    private String url;

    private Map<String, String> data;

    public FiveException(ResultEnums enums) {
        super(enums.getMsg());
        this.code = enums.getCode();
        this.url = "/index";
    }

    public FiveException(ResultEnums enums, String url) {
        super(enums.getMsg());
        this.code = enums.getCode();
        this.url = url;
    }

    public FiveException(ResultEnums enums, String url, Map<String, String> data) {
        super(enums.getMsg());
        this.code = enums.getCode();
        this.url = url;
        this.data = data;
    }
}
