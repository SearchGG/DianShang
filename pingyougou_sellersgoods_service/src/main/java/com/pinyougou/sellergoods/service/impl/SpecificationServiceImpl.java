package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.groupEntity.Specification;
import com.pingyougou.mapper.TbSpecificationMapper;
import com.pingyougou.mapper.TbSpecificationOptionMapper;
import com.pingyougou.pojo.TbSpecification;
import com.pingyougou.pojo.TbSpecificationExample;
import com.pingyougou.pojo.TbSpecificationOption;
import com.pingyougou.pojo.TbSpecificationOptionExample;
import com.pingyougou.sellergoods.SpecificationService;
import com.pingyougou.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;


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

    /**
     * 新增规格信息
     * @param specification
     */
    @Override
    public void add(Specification specification) {
            //添加规格
            TbSpecification tbSpecification = specification.getTbSpecification();
            specificationMapper.insert(tbSpecification);

            //添加规格选项
            List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();

                for (TbSpecificationOption specificationOption : specificationOptions) {
                    //关联规格
                   specificationOption.setSpecId(tbSpecification.getId());
                    specificationOptionMapper.insert(specificationOption);
                }
    }

    /**
     * 数据回显
     * @param id
     * @return
     */
    @Override
    public Specification findOne(long id) {
        //根据id查询数据存到组合实体类
        Specification specification = new Specification();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specification.setTbSpecification(tbSpecification);

        //条件查询规格选项
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);
        specification.setSpecificationOptions(specificationOptions);

        return specification;
    }

    /**
     * 修改规则信息及规格选项
     * @param specification
     */
    @Override
    public void update(Specification specification) {
        //修改规格信息
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //拼装条件进行删除操作
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);

        //进行规则选项的添加
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();
        for (TbSpecificationOption specificationOption : specificationOptions) {
            specificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }
    }

    /**
     * 删除规格数据
     * @param ids
     */
    @Override
    public void delete(long[] ids) {
        for (long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);

            //拼装条件进行删除操作
            TbSpecificationOptionExample example=new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }


    }


}
