package com.pingyougou.mapper;

import com.pingyougou.pojo.TbBrand;

import java.util.List;

public interface TbBrandMapper {

    /**
     * 查询所有
     * @return
     */
    public List<TbBrand> findAll();

    public void insert(TbBrand tbBrand);
}
