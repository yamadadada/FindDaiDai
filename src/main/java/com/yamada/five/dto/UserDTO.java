package com.yamada.five.dto;

import com.yamada.five.enums.UserStatusEnum;
import lombok.Data;

@Data
public class UserDTO {

    private Long userId;

    private String openid;

    private String name;

    private AddressDTO addressDTO;

    private String phone;

    private String studentId;

    private UserStatusEnum userStatusEnum;
}
