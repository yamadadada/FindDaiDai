package com.yamada.five.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    NEW(0, "新订单"),
    HAVE_PAY(1, "已付款"),
    HAVE_RECEIPT(2, "已接单"),
    COMPLETE(3, "已完成"),
    CANCEL(4, "已取消")
    ;

    private Integer code;

    private String message;
}
