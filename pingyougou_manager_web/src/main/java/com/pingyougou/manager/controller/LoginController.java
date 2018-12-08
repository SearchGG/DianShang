package com.pingyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    /**
     *  获取用户名 展示在页面
     */
    @RequestMapping("/getName")
    public Map<String,String> getName(){

        Map<String,String> map=new HashMap<>();
        //基于安全框架获取用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName",loginName);
        return map;
    }


}
