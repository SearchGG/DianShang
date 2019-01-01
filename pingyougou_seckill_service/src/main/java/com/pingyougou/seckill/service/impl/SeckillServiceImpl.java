package com.pingyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pingyougou.mapper.TbSeckillGoodsMapper;
import com.pingyougou.mapper.TbSeckillOrderMapper;
import com.pingyougou.pojo.TbSeckillGoods;
import com.pingyougou.pojo.TbSeckillOrder;
import com.pingyougou.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import java.util.Date;
import java.util.List;
@Service
@Transactional
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<TbSeckillGoods> findSeckillGoodsFromRedis() {
        return redisTemplate.boundHashOps("seckill_goods").values();
    }

    @Override
    public TbSeckillGoods findBySeckillGoodsId(Long seckillGoodsId) {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(seckillGoodsId);
    }

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void saveSeckillOrder(Long seckillGoodsId, String userId) {

        //从缓存中获取秒杀商品
        TbSeckillGoods seckillGoods=  (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(seckillGoodsId);

        if(seckillGoods==null || seckillGoods.getStockCount()<=0){
            throw new RuntimeException("商品售罄");
        }


        //组装秒杀订单数据
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        /*
         * `id` bigint(20) NOT NULL COMMENT '主键',
         `seckill_id` bigint(20) DEFAULT NULL COMMENT '秒杀商品ID',
         `money` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
         `user_id` varchar(50) DEFAULT NULL COMMENT '用户',
         `seller_id` varchar(50) DEFAULT NULL COMMENT '商家',
         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
         `status` varchar(1) DEFAULT NULL COMMENT '状态',
         */
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(seckillGoodsId);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(userId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("1");//未支付

        //设置秒杀商品库存减一
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        //保存秒杀订单
        seckillOrderMapper.insert(seckillOrder);
        if(seckillGoods.getStockCount()<=0){
            //商品售罄，没有库存后，需要更新数据库中秒杀商品库存数据
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);

            //清除redis中该商品
            redisTemplate.boundHashOps("seckill_goods").delete(seckillGoodsId);
        }
        //秒选下单成功后，扣减库存
        redisTemplate.boundHashOps("seckill_goods").put(seckillGoodsId,seckillGoods);

    }
}
