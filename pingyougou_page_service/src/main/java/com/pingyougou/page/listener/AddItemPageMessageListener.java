package com.pingyougou.page.listener;

import com.pingyougou.groupEntity.Goods;
import com.pingyougou.page.service.ItemPageSerivce;
import com.pingyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddItemPageMessageListener implements MessageListener {

    @Autowired
    private ItemPageSerivce itemPageSerivce;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();

            // 第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是freemarker 的版本号。
            Configuration configuration = freeMarkerConfig.getConfiguration();
           // 第四步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate("item.ftl");
            // 第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
            Goods goods = itemPageSerivce.findOne(Long.parseLong(goodsId));
            List<TbItem> itemList = goods.getItemList();
            Map<String,Object> map = new HashMap<>();
            for (TbItem item : itemList) {
                map.put("goods",goods);
                map.put("item",item);

                //  第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
                Writer out= new FileWriter("F:/72pingyougou/item72/"+item.getId()+".html");
                //        第七步：调用模板对象的 process 方法输出文件。
                template.process(map,out);
                //        第八步：关闭流
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
