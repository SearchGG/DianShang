package com.pingyougou.sellergoods;

import com.pingyougou.pojo.TbBrand;
import com.pingyougou.utils.PageResult;

import java.util.List;

public interface BrandService {

    //查询所有
    public List<TbBrand> findAll();

    //分页查询
    public PageResult findPage(int pageNum,int pageSize);
    public PageResult findPage(int pageNum,int pageSize,TbBrand tbBrand);

    //添加品牌
    public void add(TbBrand tbBrand);
    //修改
    public void update(TbBrand tbBrand);
    //根据id查询
    public TbBrand findOne(Long id);

    //批量删除
    void delete(Long[] ids);
}
