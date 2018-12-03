package com.pingyougou.sellergoods;

import com.pingyougou.pojo.TbBrand;
import com.pingyougou.utils.PageResult;

import java.util.List;

public interface TbBrandService {

    public List<TbBrand> findAll();

    public PageResult findPage(int pageNum,int pageSize);
}
