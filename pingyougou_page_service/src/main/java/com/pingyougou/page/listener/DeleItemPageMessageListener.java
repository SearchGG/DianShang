package com.pingyougou.page.listener;

import com.pingyougou.mapper.TbItemMapper;
import com.pingyougou.pojo.TbItem;
import com.pingyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

public class DeleItemPageMessageListener implements MessageListener {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for (TbItem item : itemList) {
                new File("F:/72pingyougou/item72/"+item.getId()+".html").delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
