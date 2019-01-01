package com.pingyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.pojo.TbSeckillGoods;
import com.pingyougou.seckill.service.SeckillService;

import com.pingyougou.utils.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Reference
    private SeckillService seckillService;

    /**
     * 查询秒杀商品列表
     */
    @RequestMapping("/findSeckillGoodsList")
    public List<TbSeckillGoods> findSeckillGoodsList(){
        return seckillService.findSeckillGoodsFromRedis();
    }

    /**
     * 查询秒杀商品详情
     */
    @RequestMapping("/findBySeckillGoodsId")
    public TbSeckillGoods findBySeckillGoodsId(Long seckillGoodsId){
        return seckillService.findBySeckillGoodsId(seckillGoodsId);
    }

    /**
     * 秒杀下单功能
     */
    @RequestMapping("/saveSeckillOrder")
    public Result saveSeckillOrder(Long seckillGoodsId){
        try {
            //基于安全获取登录人信息
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            if(userId.equals("anonymousUser")){
               return  new Result(false,"请下登录，再下单");
            }

            seckillService.saveSeckillOrder(seckillGoodsId,userId);
            return new Result(true,"秒杀下单成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"秒杀下单失败");
        }
    }

}
