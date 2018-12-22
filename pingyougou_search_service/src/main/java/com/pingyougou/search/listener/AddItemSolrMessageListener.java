package com.pingyougou.search.listener;

import com.pingyougou.mapper.TbItemMapper;
import com.pingyougou.pojo.TbItem;
import com.pingyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class AddItemSolrMessageListener implements MessageListener {

   @Autowired
   private TbItemMapper itemMapper;

   @Autowired
   private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //获取商品id
        TextMessage textMessage=(TextMessage)message;
        try {
            //上架item数据
            String goodId = textMessage.getText();
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(Long.parseLong(goodId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            //同步索引库
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
