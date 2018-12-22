package com.pingyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pingyougou.groupEntity.Goods;
import com.pingyougou.mapper.*;
import com.pingyougou.pojo.*;
import com.pingyougou.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.pojo.TbGoodsExample.Criteria;
import com.pingyougou.sellergoods.GoodsService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //保存TbGoods数据
        TbGoods tbGoods = goods.getGoods();
        //未审核状态
        String isEnableSpec = tbGoods.getIsEnableSpec();
        tbGoods.setAuditStatus("0");
//		tbGoods.setIsDelete("0");
        goodsMapper.insert(tbGoods);
        //保存TbGoodsDesc数据
        TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();
        //设置商品id
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(tbGoodsDesc);

        if (tbGoods.getIsEnableSpec().equals("1")) {//启用规格
            for (TbItem item : goods.getItemList()) {
                //标题
                String title = goods.getGoods().getGoodsName();

                Map<String, Object> specMap = JSON.parseObject(item.getSpec());
                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                    item.setTitle(title);
                }
                setItemValue(goods, item);
                itemMapper.insert(item);
            }
        }else {//不启用规格
                TbItem item = new TbItem();
                String title = goods.getGoods().getGoodsName();
                item.setTitle(title);
                setItemValue(goods, item);
                item.setSpec("{}");
                item.setPrice(tbGoods.getPrice());
                item.setNum(999);
                item.setStatus("1");
                item.setIsDefault("1");
                itemMapper.insert(item);



        }

    }

    private void setItemValue(Goods goods, TbItem item) {
        item.setGoodsId(goods.getGoods().getId());//商品 SPU 编号
        item.setSellerId(goods.getGoods().getSellerId());//商家编号
        item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3 级）
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//修改日期
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //图片地址（取 spu 的第一个图片）
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        //删除的不显示设置为删除
        criteria.andIsDeleteIsNull();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 商品审核
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);

            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination addItemSolrDestination;

    @Autowired
    private Destination deleItemSolrDestination;

    @Autowired
    private Destination addItemPageDestination;

    @Autowired
    private Destination deleItemPageDestination;
    /**
     * 商品上下架
     * @param ids
     */
    @Override
    public void updateIsMarketable(Long[] ids, String isMarketable) {
        for (Long id : ids) {

            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //只有审核通过的
            if ("1".equals(tbGoods.getAuditStatus())){

                tbGoods.setIsMarketable(isMarketable);
                goodsMapper.updateByPrimaryKey(tbGoods);
                //上架
                if ("1".equals(isMarketable)){
                    jmsTemplate.send(addItemSolrDestination,new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id+"");
                        }
                    });
                    //静态页面生成
                    jmsTemplate.send(addItemPageDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id+"");
                        }
                    });

                }
                //下架
                if ("0".equals(isMarketable)){
                    jmsTemplate.send(deleItemSolrDestination,new MessageCreator() {
                                @Override
                                public Message createMessage(Session session) throws JMSException {
                                    return session.createTextMessage(id+"");
                                }
                    });
                    //静态页面删除
                    jmsTemplate.send(deleItemPageDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id+"");
                        }
                    });
                }

            }else{
                throw new RuntimeException("该商品未审核");
            }
        }
    }
}
