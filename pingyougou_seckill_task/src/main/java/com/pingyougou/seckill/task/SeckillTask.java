package com.pingyougou.seckill.task;

import com.pingyougou.mapper.TbSeckillGoodsMapper;
import com.pingyougou.pojo.TbSeckillGoods;
import com.pingyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 定时任务类，通过秒杀商品到redis缓存
 */
@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //@Scheduled定时器注解，需要配置cron表达式，完成定时操作
    @Scheduled(cron = "0/10 * * * * ?")//含义：每个10秒执行一次
   // @Scheduled(cron = "0 55 9 * * ?")//含义：每天9点55分同步10点开始的秒杀商品
    public void synchronizeSeckillGoodsToRedis() {
        /**
         * 什么条件的商品能够展示在秒杀首页呢?
         审核通过
         有库存
         当前时间大于开始时间,并小于秒杀结束时间
         */
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1").
                andStockCountGreaterThan(0).
                andStartTimeLessThanOrEqualTo(new Date()).
                andEndTimeGreaterThanOrEqualTo(new Date());
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            //将秒杀商品同步缓存中
            redisTemplate.boundHashOps("seckill_goods").put(seckillGoods.getId(),seckillGoods);
        }
        System.out.println("synchronizeSeckillGoodsToRedis finish ......");
    }

}
