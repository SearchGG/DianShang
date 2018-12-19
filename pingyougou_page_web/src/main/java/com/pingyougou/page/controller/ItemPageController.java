package com.pingyougou.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.groupEntity.Goods;
import com.pingyougou.page.service.ItemPageSerivce;
import com.pingyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/itemPage")
public class ItemPageController {

    @Reference
    private ItemPageSerivce itemPageSerivce;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @RequestMapping("/getHtml")
    public String getHtml(Long goodsId){
        try {
            Configuration configuration=freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Goods goods = itemPageSerivce.findOne(goodsId);
            List<TbItem> itemList = goods.getItemList();
            Map<String,Object> map=new HashMap<>();
            for (TbItem item : itemList) {
                map.put("item",item);
                map.put("goods",goods);
                Writer out=new FileWriter("F:/72pingyougou/item72/"+item.getId()+".html");
                template.process(map,out);
                out.close();
            }
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }


}
