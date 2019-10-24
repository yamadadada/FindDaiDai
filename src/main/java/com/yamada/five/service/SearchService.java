package com.yamada.five.service;

import com.yamada.five.pojo.EsItem;
import com.yamada.five.pojo.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {

    List<EsItem> findItemByItemNameOrOrderName(String itemName, String orderName);

    Page<EsItem> findItemByItemNameOrOrderName(String itemName, String orderName, Pageable pageable);

    void saveEsItem(EsItem esItem);
}
