package com.yamada.five.service.Impl;

import com.yamada.five.pojo.EsItem;
import com.yamada.five.repository.EsItemRepository;
import com.yamada.five.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private EsItemRepository itemRepository;

    @Override
    public List<EsItem> findItemByItemNameOrOrderName(String itemName, String orderName) {
        return itemRepository.findByItemNameOrOrderName(itemName, orderName);
    }

    @Override
    public Page<EsItem> findItemByItemNameOrOrderName(String itemName, String orderName, Pageable pageable) {
        return itemRepository.findByItemNameOrOrderName(itemName, orderName, pageable);
    }

    @Override
    @RabbitListener(queues = "esitem")
    public void saveEsItem(EsItem esItem) {
        log.info("【消息队列】接受到id为：" + esItem.getItemId() + "的项");
//        itemRepository.save(esItem);
    }
}
