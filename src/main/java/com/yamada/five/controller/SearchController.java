package com.yamada.five.controller;

import com.yamada.five.pojo.EsItem;
import com.yamada.five.service.SearchService;
import com.yamada.five.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 实时搜索
     * @param p
     * @return
     */
    @GetMapping("")
    @ResponseBody
    public Object realSearch(@RequestParam(value = "p") String p) {
        List<EsItem> itemList = searchService.findItemByItemNameOrOrderName(p, p);
        return ResultVOUtil.success(itemList);
    }
}
