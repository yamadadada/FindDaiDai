package com.yamada.five.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Address {

    @TableId(type = IdType.AUTO)
    private Long addressId;

    private Long userId;

    private Long addressDetailId;

    private String addressName;

    private String addressPhone;
}
