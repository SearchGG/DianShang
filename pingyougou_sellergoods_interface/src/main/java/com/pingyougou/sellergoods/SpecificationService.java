package com.pingyougou.sellergoods;

import com.pingyougou.groupEntity.Specification;
import com.pingyougou.pojo.TbSpecification;
import com.pingyougou.utils.PageResult;

import java.util.List;

public interface SpecificationService {

    //条件分页查询
    public PageResult findPage(Integer pageNum, Integer pageSize, TbSpecification specification);

    //新增规格信息
    public void add(Specification specification);

}
