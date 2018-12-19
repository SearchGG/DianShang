package com.pingyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {


    @Reference
    private SearchService searchService;

    /**
     * 商品搜索
     * @param searchMap
     * @return
     */
    @RequestMapping("/searchItem")
    public Map<String,Object> searchItem(@RequestBody Map searchMap){
       return searchService.search(searchMap);
    }

}
