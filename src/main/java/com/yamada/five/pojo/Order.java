package com.yamada.five.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("food_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long orderId;

    // 订单描述
    private String orderName;

    // 收货地址ID
    private Long addressId;

    // 下单时间
    private Date placeTime;

    // 下单用户ID
    private Long placeUserId;

    // 接单时间
    private Date receiptTime;

    // 接单用户ID
    private Long receiptUserId;

    // 订单状态
    private Integer orderStatus;

    // 物品总额
    private BigDecimal itemAmount;

    // 运费
    private BigDecimal freight;

    // 订单总额
    private BigDecimal orderAmount;

    // 订单截止时间
    private Date deadline;

    // 订单备注
    private String orderRemark;

    // 下单人评价
    private Integer placeRemark;

    // 接单人评价
    private Integer receiptRemark;
}
