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
    public List<TbBrand> findAll() {
        return tbBrandService.findAll();

    }

    /**
     * 分页查询
     *
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {

        return tbBrandService.findPage(page, rows);
    }

    /**
     * 添加品牌信息
     *
     * @param tbBrand
     */
    @RequestMapping("/add")
    public pingyougouResult add(@RequestBody TbBrand tbBrand) {
        try {
            tbBrandService.add(tbBrand);
            return new pingyougouResult(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pingyougouResult(false, "添加失败");
        }
    }

    /**
     * 根据id获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id) {
        return tbBrandService.findOne(id);

    }

    /**
     * 修改信息
     *
     * @param tbBrand
     * @return
     */
    @RequestMapping("/update")
    public pingyougouResult update(@RequestBody TbBrand tbBrand) {
        try {
            tbBrandService.update(tbBrand);
            return new pingyougouResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pingyougouResult(false, "修改失败");
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public pingyougouResult delete(Long[] ids){
        try {
            tbBrandService.delete(ids);
            return new pingyougouResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pingyougouResult(false, "删除失败");
        }
    }

    /**
     * 条件查询+分页
     * @param brand
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int page, int rows ) {
        return tbBrandService.findPage(brand, page, rows);

    }
}
