package com.pingyougou.manager.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.groupEntity.Specification;
import com.pingyougou.pojo.TbSpecification;
import com.pingyougou.sellergoods.SpecificationService;
import com.pingyougou.utils.PageResult;
import com.pingyougou.utils.pingyougouResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public pingyougouResult add(@RequestBody Specification specification){
        try {
            specificationService.add(specification);
            return new pingyougouResult(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new pingyougouResult(false,"添加失败");
        }
    }


}
