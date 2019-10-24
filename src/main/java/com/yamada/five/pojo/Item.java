package com.yamada.five.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {

    @TableId(type = IdType.AUTO)
    private Long itemId;

    private Long orderId;

    private String itemName;

    private Integer number;

    private BigDecimal price;
}
