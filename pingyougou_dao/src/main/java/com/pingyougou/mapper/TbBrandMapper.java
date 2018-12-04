package com.pingyougou.mapper;

import com.pingyougou.pojo.TbBrand;

import java.util.List;

public interface TbBrandMapper {

    /**
     * 查询所有
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 添加品牌信息
     * @param tbBrand
     */
    public void insert(TbBrand tbBrand);

    /**
     * 修改信息
     * @param tbBrand
     */
    public void updateByBrandiId(TbBrand tbBrand);

    /**
     * 根据id查询数据 回显
     * @param id
     * @return
     */
    public TbBrand findOneById(Long id);

}
