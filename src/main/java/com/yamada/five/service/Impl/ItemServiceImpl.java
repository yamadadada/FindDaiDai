package com.yamada.five.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yamada.five.mapper.ItemMapper;
import com.yamada.five.pojo.Item;
import com.yamada.five.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public Long insert(Item item) {
        itemMapper.insert(item);
        return item.getItemId();
    }

    @Override
    public List<Item> getListByOrderId(Long orderId) {
        QueryWrapper<Item> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        return itemMapper.selectList(wrapper);
    }
}
