package com.yamada.five.service;

import com.yamada.five.pojo.Item;

import java.util.List;

public interface ItemService {

    Long insert(Item item);

    List<Item> getListByOrderId(Long orderId);
}
