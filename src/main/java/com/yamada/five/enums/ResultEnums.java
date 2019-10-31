package com.yamada.five.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultEnums {

    OK(200, "成功"),
    USER_EXIST(1000, "该用户已存在！"),
    REGISTER_ERROR(1001, "注册失败！请稍后再试"),
    ADDRESS_INSERT_ERROR(1002, "新增地址失败！请稍后再试"),
    USER_UPDATE_ERROR(1003, "用户信息更新失败！请稍后再试"),
    NOT_AUTHORITY(1004, "你无权限进行该操作！"),
    ORDER_STATUS_ERROR(1005, "订单状态错误！请稍后再试"),
    ORDER_OPERATION_ERROR(1006, "订单操作失败！请稍后再试"),
    PASSWORD_ERROR(1007, "密码验证失败！"),
    USER_NOT_EXIST(1008, "该用户不存在！"),
    ORDER_NOT_EXIST(1009, "该订单不存在！"),
    ADDRESS_NOT_EXIST(1010, "该地址不存在！"),
    ADDRESS_DELETE_ERROR(1011, "地址删除错误！"),
    ADDRESS_UPDATE_ERROR(1012, "地址更新失败！"),
    PHONE_ERROR(1013, "手机号格式不正确！"),
    MSM_OFTEN(1014, "你的请求太频繁！请稍后再试"),
    FORM_VERIFICATION_ERROR(1015, "你输入的信息有误！"),
    MSM_VERIFICATION_ERROR(1016, "短信验证码不正确！"),
    ORDER_EXPIRED(1017, "订单已过期"),
    NOT_PERMISSION(1018, "你没有权限接单"),
    LOGIN_FAIL(1019, "登录失败")
    ;

    private Integer code;

    private String msg;
}
