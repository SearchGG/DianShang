package com.pingyougou.seckill.service;

import com.pingyougou.pojo.TbSeckillGoods;

import java.util.List;

public interface SeckillService {

    /**
     * 查询秒杀商品列表
     */
    public List<TbSeckillGoods> findSeckillGoodsFromRedis();

    /**
     * 查询秒杀商品详情
     */
    public TbSeckillGoods findBySeckillGoodsId(Long seckillGoodsId);

    /**
     * 秒杀下单
     */
    public void saveSeckillOrder(Long seckillGoodsId,String userId);
}
