package com.pingyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.pojo.TbBrand;
import com.pingyougou.sellergoods.TbBrandService;
import com.pingyougou.utils.PageResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class brandController {

    @Reference
    private TbBrandService tbBrandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return tbBrandService.findAll();

    }

    @RequestMapping("/findPage")
    public PageResult findPage(int page,int rows){

        return tbBrandService.findPage(page,rows);
    }


}
