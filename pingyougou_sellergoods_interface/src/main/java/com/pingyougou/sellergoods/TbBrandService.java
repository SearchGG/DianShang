package com.pingyougou.sellergoods;

import com.pingyougou.pojo.TbBrand;
import com.pingyougou.utils.PageResult;

import java.util.List;

public interface TbBrandService {

    //查询所有
    public List<TbBrand> findAll();

    //分页查询
    public PageResult findPage(int pageNum,int pageSize);

    //添加品牌
    public void add(TbBrand tbBrand);
}
