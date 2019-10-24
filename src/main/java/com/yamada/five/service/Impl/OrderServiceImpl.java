package com.yamada.five.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yamada.five.bo.UserInfo;
import com.yamada.five.dto.OrderDTO;
import com.yamada.five.enums.OrderStatusEnum;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveApiException;
import com.yamada.five.exception.FiveException;
import com.yamada.five.form.OrderForm;
import com.yamada.five.mapper.AddressMapper;
import com.yamada.five.mapper.ItemMapper;
import com.yamada.five.mapper.OrderMapper;
import com.yamada.five.mapper.UserMapper;
import com.yamada.five.pojo.Address;
import com.yamada.five.pojo.Item;
import com.yamada.five.pojo.Order;
import com.yamada.five.pojo.User;
import com.yamada.five.service.AddressService;
import com.yamada.five.service.OrderService;
import com.yamada.five.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Override
    public Order getOne(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new FiveException(ResultEnums.ORDER_NOT_EXIST);
        }
        return order;
    }

    @Override
    public List<Order> getReceiptList(Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("receipt_user_id", userId);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public List<Order> getPlaceList(Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("place_user_id", userId);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public List<Order> getByOrderStatus(OrderStatusEnum orderStatusEnum) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_status", orderStatusEnum.getCode());
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Long publish(OrderForm form) {
        // 登录验证
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userInfo.getUserId();
        Order order = new Order();
        BeanUtils.copyProperties(form, order);
        order.setPlaceTime(new Date());
        order.setPlaceUserId(userId);
        order.setOrderStatus(OrderStatusEnum.NEW.getCode());
        BigDecimal itemAmount = new BigDecimal(0);
        List<Item> itemList = form.getItemList();
        for (Item item: itemList) {
            BigDecimal bigDecimal = item.getPrice().multiply(new BigDecimal(item.getNumber()));
            itemAmount = itemAmount.add(bigDecimal);
        }
        order.setItemAmount(itemAmount);
        order.setOrderAmount(itemAmount.add(order.getFreight()));
        Integer result = orderMapper.insert(order);
        if (result == 0) {
            throw new FiveApiException(ResultEnums.ORDER_OPERATION_ERROR);
        }

        return order.getOrderId();
    }

    @Override
    public void updateById(Order order) {
        Integer result = orderMapper.updateById(order);
        if (result == 0) {
            throw new FiveException(ResultEnums.ORDER_OPERATION_ERROR);
        }
    }

    @Override
    public OrderDTO orderToOrderDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        User user1 = userMapper.selectById(order.getPlaceUserId());
        User user2 = userMapper.selectById(order.getReceiptUserId());
        orderDTO.setPlaceUserDTO(userService.userToUserDTO(user1));
        orderDTO.setReceiptUserDTO(userService.userToUserDTO(user2));
        Address address = addressMapper.selectById(order.getAddressId());
        orderDTO.setAddressDTO(addressService.addressToAddressDTO(address));

        for (OrderStatusEnum statusEnum: OrderStatusEnum.values()) {
            if (statusEnum.getCode().equals(order.getOrderStatus())) {
                orderDTO.setOrderStatusEnum(statusEnum);
                break;
            }
        }
        return orderDTO;
    }

    @Override
    public List<OrderDTO> orderListToOrderDTO(List<Order> orderList) {
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order order: orderList) {
            orderDTOList.add(orderToOrderDTO(order));
        }
        return orderDTOList;
    }

    @Override
    public OrderDTO addItemList(OrderDTO orderDTO) {
        QueryWrapper<Item> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderDTO.getOrderId());
        List<Item> itemList = itemMapper.selectList(wrapper);
        orderDTO.setItemList(itemList);
        return orderDTO;
    }

    @Override
    public List<OrderDTO> addItemList(List<OrderDTO> orderDTOList) {
        return orderDTOList.stream().map(this::addItemList).collect(Collectors.toList());
    }
}
