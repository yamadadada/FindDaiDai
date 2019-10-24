package com.yamada.five.service.Impl;

import com.yamada.five.pojo.EsItem;
import com.yamada.five.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceImplTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void findItemByItemNameOrOrderName() {
    }

    @Test
    public void findItemByItemNameOrOrderName1() {
    }

    @Test
    public void saveEsItem() {
        EsItem esItem = new EsItem();
        esItem.setItemId(5L);
        esItem.setItemName("测试1");
        esItem.setOrderId(15L);
        esItem.setOrderName("测试2");
        searchService.saveEsItem(esItem);
    }
}