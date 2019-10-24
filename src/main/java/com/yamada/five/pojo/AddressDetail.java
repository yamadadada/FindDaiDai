package com.yamada.five.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AddressDetail {

    @TableId(type = IdType.AUTO)
    private Long addressDetailId;

    private Integer area;

    private String building;

    private String room;
}
