package com.pingyougou.manager.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.groupEntity.Specification;
import com.pingyougou.pojo.TbSpecification;
import com.pingyougou.sellergoods.SpecificationService;
import com.pingyougou.utils.PageResult;
import com.pingyougou.utils.pygResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /**
     * 条件查询
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows,@RequestBody TbSpecification specification){
        return specificationService.findPage(page,rows,specification);
    }

    /**
     * 新增规格信息
     * @param specification
     */
    @RequestMapping("/add")
    public pygResult add(@RequestBody Specification specification){
        try {
            specificationService.add(specification);
            return new pygResult(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pygResult(false,"添加失败");
        }
    }

    /**
     * 根据id进行数据回显
     * @return
     */
    @RequestMapping("/findOne")
    public Specification findOne(Long id){
       return specificationService.findOne(id);
    }

    /**
     *修改信息
     * @param specification
     * @return
     */
    @RequestMapping("/update")
    public pygResult update(@RequestBody Specification specification){
        try {
            specificationService.update(specification);
            return new pygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pygResult(false, "修改失败");
        }
    }

    /**
     * 删除数据
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public pygResult delete(long[] ids){
        try {
            specificationService.delete(ids);
            return new pygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pygResult(false, "删除失败");
        }
    }
    /**
     * 规格下拉框
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }
}
