package com.pingyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pingyougou.groupEntity.Goods;
import com.pingyougou.mapper.TbGoodsDescMapper;
import com.pingyougou.mapper.TbGoodsMapper;
import com.pingyougou.mapper.TbItemCatMapper;
import com.pingyougou.mapper.TbItemMapper;
import com.pingyougou.page.service.ItemPageSerivce;
import com.pingyougou.pojo.TbGoods;
import com.pingyougou.pojo.TbGoodsDesc;
import com.pingyougou.pojo.TbItem;
import com.pingyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemPageServiceImpl implements ItemPageSerivce {

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;


    @Override
    public Goods findOne(Long goodId) {
        //根据goodId查询商品
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodId);
        //根据goodId查询GoodDesc
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodId);

        //根据分类id查询分类
        String category1Name = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
        String category2Name = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
        String category3Name = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

        Map<String,String> categoryMap=new HashMap<>();
        categoryMap.put("category1Name",category1Name);
        categoryMap.put("category2Name",category2Name);
        categoryMap.put("category3Name",category3Name);


        //根据goodid查询item表
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodId);
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);


        //查询结果封装到goods组合实体类中
        Goods goods=new Goods();
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(tbItems);
        goods.setCategoryMap(categoryMap);

        return goods;
    }
}
