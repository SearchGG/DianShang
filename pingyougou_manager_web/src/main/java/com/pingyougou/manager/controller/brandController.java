package com.pingyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.pojo.TbBrand;
import com.pingyougou.sellergoods.TbBrandService;
import com.pingyougou.utils.PageResult;
import com.pingyougou.utils.pingyougouResult;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * 分页查询
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int rows){

        return tbBrandService.findPage(page,rows);
    }

    /**
     * 添加品牌信息
     * @param tbBrand
     */
    @RequestMapping("/add")
    public pingyougouResult add(@RequestBody TbBrand tbBrand){
        try {
            tbBrandService.add(tbBrand);
            return new pingyougouResult(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pingyougouResult(false,"添加失败");
        }
    }

}
