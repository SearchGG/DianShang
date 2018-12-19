package com.itheiam.demo;


import com.pingyougou.pojo.TbItem;
import com.pingyougou.solr.util.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class SolrDemo {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrUtil solrUtil;
    /**
     * 导入商品测试
     *
     */
    @Test
    public void dataImport(){
        solrUtil.dataImport();
    }


    /**
     * 新增
     */
    @Test
    public void saveItem() {

        TbItem item = new TbItem();
        item.setTitle("我要把你打的喵喵叫");
        item.setId(1L);
        item.setSeller("xinxin");
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    /**
     * 基于id查询
     */
    @Test
    public void queryId() {
        TbItem item = solrTemplate.getById(1l, TbItem.class);

        System.out.println(item.getId() + " " + item.getTitle());
    }

    /**
     * 基于id删除
     */
    @Test
    public void deleteId() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();

    }
    /**
     * 删除所有
     */
    @Test
    public void deleteAll() {
        SolrDataQuery query=new SimpleQuery("*.*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 分页查询
     */
    public void  findPage(){

        Query query=new SimpleQuery("*.*");
        //查询条件
        query.setOffset(0);
        query.setRows(5);

        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);
        //总页数
        items.getTotalPages();
        //总条数
        items.getTotalElements();
    }
    /**
     * 条件查询
     */
    @Test
    public void  find(){

        Query query=new SimpleQuery("*.*");
        //设置条件查询
        Criteria criteria=new Criteria("item_title").contains("9").and("item_id").contains("5");
        query.addCriteria(criteria);
        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);

    }


}
