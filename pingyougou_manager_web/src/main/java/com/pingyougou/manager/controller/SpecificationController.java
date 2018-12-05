package com.pingyougou.manager.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.pojo.TbSpecification;
import com.pingyougou.sellergoods.SpecificationService;
import com.pingyougou.utils.PageResult;
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

}
