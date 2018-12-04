package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.mapper.TbBrandMapper;
import com.pingyougou.pojo.TbBrand;
import com.pingyougou.sellergoods.TbBrandService;
import com.pingyougou.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements TbBrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.findAll();
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page= (Page<TbBrand>)tbBrandMapper.findAll();

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {

        return null;
    }

    @Override
    public void add(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByBrandiId(tbBrand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return tbBrandMapper.findOneById(id);
    }

    @Override
    public void delete(Long[] ids) {

        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                tbBrandMapper.deleteById(id);
            }
        }
    }
}
