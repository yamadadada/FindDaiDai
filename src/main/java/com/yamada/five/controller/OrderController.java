package com.yamada.five.controller;

import com.yamada.five.bo.UserInfo;
import com.yamada.five.dto.OrderDTO;
import com.yamada.five.enums.AreaEnum;
import com.yamada.five.enums.OrderStatusEnum;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveException;
import com.yamada.five.form.OrderForm;
import com.yamada.five.pojo.EsItem;
import com.yamada.five.pojo.Item;
import com.yamada.five.pojo.Order;
import com.yamada.five.pojo.User;
import com.yamada.five.service.ItemService;
import com.yamada.five.service.OrderService;
import com.yamada.five.service.UserService;
import com.yamada.five.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("")
    public String order(Map<String, Object> map) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //接单List
        List<Order> receiptList = orderService.getReceiptList(userInfo.getUserId());
        //下单list
        List<Order> placeList = orderService.getPlaceList(userInfo.getUserId());
        map.put("receiptList", orderService.orderListToOrderDTO(receiptList));
        map.put("placeList", orderService.orderListToOrderDTO(placeList));
        map.put("userId", userInfo.getUserId());
        return "order/order";
    }

    @GetMapping("{orderId}")
    public String orderDetail(@PathVariable("orderId") Long orderId, @RequestParam(value = "reurl", defaultValue = "index") String reurl, Map<String, Object> map) {
        Order order = orderService.getOne(orderId);
        OrderDTO orderDTO = orderService.orderToOrderDTO(order);
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = null;
        if (userInfo != null) {
            userId = userInfo.getUserId();
        }
        List<Item> itemList = itemService.getListByOrderId(orderId);
        map.put("orderDTO", orderDTO);
        map.put("userId", userId);
        map.put("itemList", itemList);
        map.put("reurl", reurl);
        return "order/detail";
    }

    @RequestMapping("/toPublish")
    public String toPublish(Map<String, Object> map) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userInfo.getUserId());
        map.put("userId", user.getUserId());
        map.put("areaEnumList", AreaEnum.values());
        return "order/publish";
    }

    /**
     * 发布订单
     * @param form
     * @return
     */
    @PostMapping("")
    @ResponseBody
    public Object publish(@RequestBody OrderForm form) {
        // 插入order表
        Long orderId = orderService.publish(form);

        // 插入item表
        List<Item> itemList = form.getItemList();
        for (Item item: itemList) {
            item.setOrderId(orderId);
            itemService.insert(item);
            EsItem esItem = new EsItem();
            esItem.setItemId(item.getItemId());
            esItem.setItemName(item.getItemName());
            esItem.setOrderId(orderId);
            esItem.setOrderName(form.getOrderName());
            // 通过rabbitmq通知es更新数据
            rabbitTemplate.convertAndSend("esitem", esItem);
            log.info("【消息队列】已发送id为：" + esItem.getItemId() + "的项");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        return ResultVOUtil.success(map);
    }

    @GetMapping("/{orderId}/pay")
    public String pay(@PathVariable("orderId") Long orderId) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderService.getOne(orderId);
        if (!userInfo.getUserId().equals(order.getPlaceUserId())) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        if (!order.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            throw new FiveException(ResultEnums.ORDER_STATUS_ERROR);
        }
        //TODO 支付操作
        order.setOrderStatus(OrderStatusEnum.HAVE_PAY.getCode());
        orderService.updateById(order);
        return "redirect:/order/" + orderId;
    }

    @GetMapping("/{orderId}/cancel")
    public String cancel(@PathVariable("orderId") Long orderId) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderService.getOne(orderId);
        if (!userInfo.getUserId().equals(order.getPlaceUserId())) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        if (!(order.getOrderStatus().equals(OrderStatusEnum.NEW.getCode()) || order.getOrderStatus().equals(OrderStatusEnum.HAVE_PAY.getCode()))) {
            throw new FiveException(ResultEnums.ORDER_OPERATION_ERROR);
        }
        if (order.getOrderStatus().equals(OrderStatusEnum.HAVE_PAY.getCode())) {
            //TODO 退款操作
        }
        order.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        orderService.updateById(order);
        return "redirect:/order/" + orderId;
    }

    @GetMapping("/{orderId}/receipt")
    public String receipt(@PathVariable("orderId") Long orderId) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderService.getOne(orderId);
        if (userInfo.getUserId().equals(order.getPlaceUserId())) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        if (!order.getOrderStatus().equals(OrderStatusEnum.HAVE_PAY.getCode())) {
            throw new FiveException(ResultEnums.ORDER_OPERATION_ERROR);
        }
        order.setReceiptTime(new Date());
        order.setReceiptUserId(userInfo.getUserId());
        order.setOrderStatus(OrderStatusEnum.HAVE_RECEIPT.getCode());
        orderService.updateById(order);
        return "redirect:/order/" + orderId;
    }

    @GetMapping("/{orderId}/complete")
    public String complete(@PathVariable("orderId") Long orderId) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderService.getOne(orderId);
        if (!userInfo.getUserId().equals(order.getReceiptUserId())) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        if (!order.getOrderStatus().equals(OrderStatusEnum.HAVE_RECEIPT.getCode())) {
            throw new FiveException(ResultEnums.ORDER_OPERATION_ERROR);
        }
        order.setOrderStatus(OrderStatusEnum.COMPLETE.getCode());
        orderService.updateById(order);
        return "redirect:/order/" + orderId;
    }
}
