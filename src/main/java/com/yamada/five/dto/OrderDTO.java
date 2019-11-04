package com.yamada.five.dto;

import com.yamada.five.enums.OrderStatusEnum;
import com.yamada.five.pojo.Item;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Data
public class OrderDTO {

    private Long orderId;

    // 订单描述
    private String orderName;

    // 收货地址ID
    private AddressDTO addressDTO;

    // 下单时间
    private Date placeTime;

    // 下单用户ID
    private UserDTO placeUserDTO;

    // 接单时间
    private Date receiptTime;

    // 接单用户ID
    private UserDTO receiptUserDTO;

    // 订单状态
    private OrderStatusEnum orderStatusEnum;

    // 物品总额
    private BigDecimal itemAmount;

    // 运费
    private BigDecimal freight;

    // 订单总额
    private BigDecimal orderAmount;

    // 订单备注
    private String orderRemark;

    // 订单截止时间
    private Date deadline;

    //子项目List
    private List<Item> itemList;

    /**
     * 订单简要描述
     * @return
     */
    public String getOrderDescribe() {
        if (itemList == null || itemList.size() == 0) {
            return orderName;
        }
        StringJoiner sj = new StringJoiner(" ", "（", "）");
        for (Item item: itemList) {
            sj.add(item.getItemName());
        }
        return orderName + sj.toString();
    }

    /**
     * 订单中的项目简要描述
     */
    public String getItemDescribe() {
        if (itemList == null || itemList.size() == 0) {
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        for (Item item: itemList) {
            sj.add(item.getItemName());
        }
        return sj.toString();
    }
}
