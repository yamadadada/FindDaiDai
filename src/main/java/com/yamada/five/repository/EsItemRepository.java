package com.yamada.five.repository;

import com.yamada.five.pojo.EsItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EsItemRepository extends ElasticsearchRepository<EsItem, Long> {

    List<EsItem> findByItemNameOrOrderName(String itemName, String orderName);

    Page<EsItem> findByItemNameOrOrderName(String name, String orderName, Pageable pageable);
}
