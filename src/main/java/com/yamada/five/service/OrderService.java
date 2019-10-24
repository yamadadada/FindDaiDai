package com.yamada.five.service;

import com.yamada.five.dto.OrderDTO;
import com.yamada.five.enums.OrderStatusEnum;
import com.yamada.five.form.OrderForm;
import com.yamada.five.pojo.Order;

import java.util.List;
import java.util.Set;

public interface OrderService {

    Order getOne(Long orderId);

    List<Order> getReceiptList(Long userId);

    List<Order> getPlaceList(Long userId);

    List<Order> getByOrderStatus(OrderStatusEnum orderStatusEnum);

    Long publish(OrderForm form);

    void updateById(Order order);

    OrderDTO orderToOrderDTO(Order order);

    List<OrderDTO> orderListToOrderDTO(List<Order> orderList);

    OrderDTO addItemList(OrderDTO orderDTO);

    List<OrderDTO> addItemList(List<OrderDTO> orderDTOList);
}
