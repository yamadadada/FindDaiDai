package com.yamada.five.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private String openid;

    // 用户姓名
    @TableField("user_name")
    private String name;

    private Long addressId;

    private String phone;

    private String studentId;

    private String password;

    private Integer userStatus;

    //上次登录时间
    private Date lastLoginTime;
}
