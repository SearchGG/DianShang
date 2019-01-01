package com.pingyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.cart.service.CartService;


import com.pingyougou.groupEntity.Cart;
import com.pingyougou.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    /**
     * 获取sessionId方法
     */
    private String getSessionId(){
        //先尝试“cartCookie”中获取sessionId信息
        String sessionId = CookieUtil.getCookieValue(request, "cartCookie", "utf-8");
        if(sessionId==null){
            //再从浏览器获取sessionId信息
            sessionId = session.getId();
            //将从浏览器获取的sessionId保存一周
            CookieUtil.setCookie(request,response,"cartCookie",sessionId,3600*24*7,"utf-8");
        }
        return sessionId;
    }

    /**
     * 展示购物车列表数据
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取登录人用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //未登录时，基于sessionId从redis中获取购物车列表数据
        String sessionId = getSessionId();
        List<Cart> cartList_sessionId = cartService.selectCartListFromRedis(sessionId);
        if("anonymousUser".equals(username)) {//未登录
            System.out.println("selectCartListFromRedis by sessionId ....");
            return cartList_sessionId;
        }else {//已登录
            System.out.println("selectCartListFromRedis by username ....");
            //基于username从redis中获取购物车列表数据
            List<Cart> cartList_username = cartService.selectCartListFromRedis(username);
            //用户登录前，如果已经添加商品到购物车列表中。
            if(cartList_sessionId!=null && cartList_sessionId.size()>0){
                //登录后，需要将登录前的购物车列表数据合并到登录后的购物车列表中。
                cartList_username = cartService.mergeCartList(cartList_sessionId,cartList_username);
                //将合并后的结果，重新放入redis缓存中
                cartService.saveCartListToRedis(username,cartList_username);
                //清除合并前的购物车列表数据
                cartService.deleteCartList(sessionId);

            }
            return cartList_username;
        }


    }


    /**
     * 添加商品到购物车
     */
    @RequestMapping("/addItemToCartList")
    //允许http://item.pingyougou.com跨域访问当前方法
    @CrossOrigin(origins = "http://item.pingyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId, Integer num){

        //response.setHeader("Access-Control-Allow-Origin","http://item.pinyougou.com");

        try {
            //获取登录人用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(username);

            //1、查询购物车列表
            List<Cart> cartList = findCartList();
            //2、调用服务层，将商品添加到购物车列表中
            cartList = cartService.addItemToCartList(cartList,itemId,num);
            if("anonymousUser".equals(username)){//未登录
                System.out.println("saveCartListToRedis by sessionId .........");

                //未登录时，获取sessionId
                String sessionId = getSessionId();
                //3、将添加商品后的购物车列表，重新存入redis中
                cartService.saveCartListToRedis(sessionId,cartList);
            }else {
                System.out.println("saveCartListToRedis by username .........");
                //已登录
                //3、将添加商品后的购物车列表，重新存入redis中
                cartService.saveCartListToRedis(username,cartList);
            }

            return new Result(true,"添加商品到购物车成功");

        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加商品到购物车失败");
        }
    }
}
