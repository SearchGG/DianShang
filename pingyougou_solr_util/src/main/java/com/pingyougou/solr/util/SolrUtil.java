package com.pingyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pingyougou.mapper.TbItemMapper;
import com.pingyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void dataImport(){
        //查询满足条件的列表
        //上架商品导入索引库 tb_goods is_marketable='1'
        //2商品状态为1 正常状态 tb_item status='1'
        List<TbItem> itemList= itemMapper.findAllGrounding();

        for (TbItem item : itemList) {
            //当前商品规格选
            String spec = item.getSpec();
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            item.setSpecMap(specMap);
        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("ojbk");

    }


}
