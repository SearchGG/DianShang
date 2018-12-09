package com.pingyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.groupEntity.Goods;
import com.pingyougou.pojo.TbGoods;
import com.pingyougou.sellergoods.GoodsService;
import com.pingyougou.utils.PageResult;
import com.pingyougou.utils.pygResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public pygResult add(@RequestBody Goods goods){
		try {
			//基于安全框架获取登录信息
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new pygResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new pygResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public pygResult update(@RequestBody TbGoods goods){
		try {
			goodsService.update(goods);
			return new pygResult(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new pygResult(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbGoods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public pygResult delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new pygResult(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new pygResult(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
}
