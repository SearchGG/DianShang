package com.pingyougou.sellergoods;

import com.pingyougou.pojo.TbBrand;
import com.pingyougou.pojo.TbSpecification;
import com.pingyougou.utils.PageResult;

import java.util.List;

public interface SpecificationService {

    //条件分页查询
    public PageResult findPage(Integer pageNum, Integer pageSize, TbSpecification specification);

}
