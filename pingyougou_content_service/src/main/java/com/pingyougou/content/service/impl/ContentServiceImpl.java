package com.pingyougou.content.service.impl;
import java.util.List;

import com.pingyougou.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.mapper.TbContentMapper;
import com.pingyougou.pojo.TbContent;
import com.pingyougou.pojo.TbContentExample;
import com.pingyougou.pojo.TbContentExample.Criteria;
import com.pingyougou.content.service.ContentService;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//清空redis广告对应的广告列表
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//清空redis广告对应的广告列表
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
		redisTemplate.boundHashOps("content").delete(tbContent.getCategoryId());
		contentMapper.updateByPrimaryKey(content);
		//如果修改的广告数据的广告分类发生变化 还需要清空变化后的广告分类对应的广告列表数据
		if (tbContent.getCategoryId()!=content.getCategoryId()){
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);
			//清空redis广告对应的广告列表
			redisTemplate.boundHashOps("content").delete(tbContent.getCategoryId());
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		//从缓存中获取列表数据
		List<TbContent> contentList=(List<TbContent>)redisTemplate.boundHashOps("content").get(categoryId);
		if (contentList==null){
			TbContentExample example=new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			//广告可用
			criteria.andStatusEqualTo("1");
			contentList = contentMapper.selectByExample(example);
			//存入缓存中
			redisTemplate.boundHashOps("content").put(categoryId,contentList);
		}

		return contentList;
	}
}
