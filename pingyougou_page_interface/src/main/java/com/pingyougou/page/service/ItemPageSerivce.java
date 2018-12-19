package com.pingyougou.page.service;

import com.pingyougou.groupEntity.Goods;

public interface ItemPageSerivce {

    /**
     * 根据商品id查询
     * @param goodId
     * @return
     */
    public Goods findOne(Long goodId);

}
