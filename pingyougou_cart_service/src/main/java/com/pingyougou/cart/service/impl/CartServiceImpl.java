package com.pingyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pingyougou.cart.service.CartService;
import com.pingyougou.groupEntity.Cart;
import com.pingyougou.mapper.TbItemMapper;
import com.pingyougou.pojo.TbItem;
import com.pingyougou.pojo.TbOrderItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    /*
     * 添加商品到购物车实现思路：
        先根据商品id查询该商品关联的商家，是否存在于购物车列表中
         1该商家对应购物车对象不存在与购物车列表
             创建购物车对象，再存入购物车列表中
             创建购物车对象时，需要指定该购物车商家信息，以及构建购物车明细列表和购物车明细对象，
             将购物车明细对象添加到购物车明细列表中，将购物车明细列表添加到购物车对象，将购物车对象
             添加到购物车列表中

         2该商家对应购物车对象存在与购物车列表
             判断该商品是否存在于购物车商品明细列表中
                 1、如果该商品不存在于购物车明细列表
                 创建购物车明细对象，再添加到购物车明细列表中
                 2、如果该商品存在于购物车明细列表
                    修改购物车明细对象的商品数据和小计金额
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //先根据商品id查询该商品关联的商家
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        if(item==null){
            throw new RuntimeException("商品不存在");
        }

        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品无效");
        }

        String sellerId = item.getSellerId();
        //从购物车列中，基于商家id获取购物车对象
        Cart cart = searchCartBySellerId(cartList,sellerId);
        if(cart==null){//该商家对应购物车对象不存在与购物车列表
            //创建购物车对象
            cart = new Cart();
            // 创建购物车对象时，需要指定该购物车商家信息
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //构建购物车明细列表和购物车明细对象
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item,num);
            //将购物车明细对象添加到购物车明细列表中
            orderItemList.add(orderItem);
            //将购物车明细列表添加到购物车对象
            cart.setOrderItemList(orderItemList);
            //将购物车对象添加到购物车列表中
            cartList.add(cart);
        }else{//2该商家对应购物车对象存在与购物车列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //判断该商品是否存在于购物车商品明细列表中
            TbOrderItem orderItem=  searchOrderItemByItemId(orderItemList,itemId);
            if (orderItem==null) {//1、如果该商品不存在于购物车明细列表
                //构建购物车明细对象，再添加到购物车明细列表中
                orderItem = createOrderItem(item,num);
                orderItemList.add(orderItem);
            }else{//2、如果该商品存在于购物车明细列表
                // 修改购物车明细对象的商品数量和小计金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //如果商品数量小于1，移除该商品
                if(orderItem.getNum()<1){
                    orderItemList.remove(orderItem);
                }

                //如果购物车商品明细列表中，没有任何商品，需要从购物车列表，移除该购物车
                if(orderItemList.size()<=0){
                    cartList.remove(cart);
                }

            }
        }

        return cartList;
    }


    /**
     * 判断该商品是否存在于购物车商品明细列表中
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            //添加商品存在于购物车商品明细列表中
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if(num<1){
            throw  new RuntimeException("新添加商品到购物车时，商品数量不能小于1");
        }

        /*
          `item_id` bigint(20) NOT NULL COMMENT '商品id',
          `goods_id` bigint(20) DEFAULT NULL COMMENT 'SPU_ID',
          `title` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品标题',
          `price` decimal(20,2) DEFAULT NULL COMMENT '商品单价',
          `num` int(10) DEFAULT NULL COMMENT '商品购买数量',
          `total_fee` decimal(20,2) DEFAULT NULL COMMENT '商品总金额',
          `pic_path` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品图片地址',
          `seller_id` varchar(100) COLLATE utf8_bin DEFAULT NULL,
         */
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    /**
     * 从购物车列中，基于商品id获取购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {

        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }

        return null;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> selectCartListFromRedis(String sessionId) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps(sessionId).get();
        //没有购物车列表
        if(cartList == null){
            //如果为null时，避免fastjson解析异常，返回空集合[]
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String sessionId, List<Cart> cartList) {
        redisTemplate.boundValueOps(sessionId).set(cartList,7L, TimeUnit.DAYS);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username) {

        for (Cart cart : cartList_sessionId) {
            //获取需要合并的商品数量和商品id
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                Long itemId = orderItem.getItemId();
                Integer num = orderItem.getNum();
                cartList_username= addItemToCartList(cartList_username,itemId,num);
            }
        }

        return cartList_username;
    }

    @Override
    public void deleteCartList(String sessionId) {
        redisTemplate.delete(sessionId);
    }
}
