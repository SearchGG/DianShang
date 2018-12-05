package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.mapper.TbSpecificationMapper;
import com.pingyougou.pojo.TbSpecification;
import com.pingyougou.pojo.TbSpecificationExample;
import com.pingyougou.sellergoods.SpecificationService;
import com.pingyougou.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;


    /**
     * 条件分页查询
     * @param pageNum
     * @param pageSize
     * @param specification
     * @return
     */
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNum,pageSize);

        TbSpecificationExample example=new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();
        if (specification!=null){

            String specName = specification.getSpecName();
            if (specName!=null&&!"".equals(specName.trim())){
                criteria.andSpecNameLike("%"+specName+"%");
            }
        }

        Page<TbSpecification> page= (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }
}
