package com.pingyougou.search.service;

import java.util.Map;

/**
 * 商品搜索模块
 */
public interface SearchService {
    /**
     * 商品搜素
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

}
