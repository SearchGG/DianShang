package com.pingyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.pojo.TbBrand;
import com.pingyougou.sellergoods.BrandService;
import com.pingyougou.utils.PageResult;
import com.pingyougou.utils.pygResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();

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

        return brandService.findPage(page, rows);
    }

    /**
     * 添加品牌信息
     *
     * @param tbBrand
     */
    @RequestMapping("/add")
    public pygResult add(@RequestBody TbBrand tbBrand) {
        try {
            brandService.add(tbBrand);
            return new pygResult(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pygResult(false, "添加失败");
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
        return brandService.findOne(id);

    }

    /**
     * 修改信息
     *
     * @param tbBrand
     * @return
     */
    @RequestMapping("/update")
    public pygResult update(@RequestBody TbBrand tbBrand) {
        try {
            brandService.update(tbBrand);
            return new pygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pygResult(false, "修改失败");
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public pygResult delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new pygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pygResult(false, "删除失败");
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
    public PageResult search( int page, int rows, @RequestBody TbBrand brand) {
        return brandService.findPage( page,rows,brand);

    }

    /**
     * 品牌下拉框
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}
