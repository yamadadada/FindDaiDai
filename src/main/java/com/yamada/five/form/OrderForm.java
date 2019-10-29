package com.yamada.five.form;

import com.yamada.five.pojo.Item;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderForm {

    // 订单描述
    private String orderName;

    // 收货地址ID
    private Long addressId;

    //子项目
    private List<Item> itemList;

    // 运费
    private BigDecimal freight;

    // 订单截止时间
    private Date deadline;

    // 订单备注
    private String orderRemark;
}
