package com.yamada.five.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yamada.five.mapper.OrderMapper;
import com.yamada.five.pojo.Order;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void getReceiptList() {
    }

    @Test
    public void getPlaceList() {
    }

    @Test
    public void getByOrderStatus() {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_status", "1");
        List<Order> orderList = orderMapper.selectList(wrapper);
        Assert.assertEquals(0, orderList.size());
    }

    @Test
    public void orderToOrderDTO() {
    }
}