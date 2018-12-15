package com.pingyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.mapper.TbBrandMapper;
import com.pingyougou.pojo.TbBrand;
import com.pingyougou.pojo.TbBrandExample;
import com.pingyougou.sellergoods.BrandService;
import com.pingyougou.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    /**
     * 查询所有
     * @return
     */
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page= (Page<TbBrand>)brandMapper.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 条件分页查询
     * @param tbBrand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage( int pageNum, int pageSize,TbBrand tbBrand) {
        PageHelper.startPage(pageNum,pageSize);

        TbBrandExample example=new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (tbBrand!=null){
            //品牌名称
            String brandName = tbBrand.getName();
            if (brandName!=null&&!"".equals(brandName.trim())){
                criteria.andNameLike("%"+brandName+"%");
            }
            //品牌首字母
            String firstChar = tbBrand.getFirstChar();
            if (firstChar!=null&&!"".equals(firstChar.trim())){
                criteria.andFirstCharEqualTo(firstChar);
            }
        }
        Page<TbBrand> page= (Page<TbBrand>)brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 添加品牌信息
     * @param tbBrand
     */
    @Override
    public void add(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

    /**
     * 修改品牌信息
     * @param tbBrand
     */
    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    /**
     * 根据id 查询品牌信息
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
            for (Long id : ids) {
                brandMapper.deleteByPrimaryKey(id);
            }

    }
    /**
    * 列表数据
    */
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
