package com.pingyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.groupEntity.Cart;
import com.pingyougou.mapper.TbOrderItemMapper;
import com.pingyougou.mapper.TbOrderMapper;
import com.pingyougou.mapper.TbPayLogMapper;
import com.pingyougou.order.service.OrderService;
import com.pingyougou.pojo.TbOrder;
import com.pingyougou.pojo.TbOrderExample;
import com.pingyougou.pojo.TbOrderExample.Criteria;
import com.pingyougou.pojo.TbOrderItem;

import com.pingyougou.pojo.TbPayLog;
import com.pingyougou.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//订单与商家关联，订单数据有很多是来自购物车列表数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps(order.getUserId()).get();

		//支付总金额
		double totalMoney=0.00;

		List<String> ids = new ArrayList<>();
		for (Cart cart : cartList) {
			//构建订单对象
			TbOrder tbOrder = new TbOrder();
			/*需要后台组装的数据
			 `order_id` bigint(20) NOT NULL COMMENT '订单id',  //不是主键自增
			 `payment` decimal(20,2) DEFAULT NULL COMMENT '实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分',
			 `status` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价',
			 `create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
			 `update_time` datetime DEFAULT NULL COMMENT '订单更新时间',
			 `user_id` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '用户id',
			 `source_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
			 `seller_id` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',  //来着购物车
			 */
			long orderId = idWorker.nextId();
			ids.add(orderId+"");
			tbOrder.setOrderId(orderId);
			tbOrder.setStatus("1");//1、未付款
			tbOrder.setCreateTime(new Date());
			tbOrder.setUpdateTime(new Date());
			tbOrder.setUserId(order.getUserId());
			tbOrder.setSourceType("2");
			tbOrder.setSellerId(cart.getSellerId());//来着购物车

			/*页面传递的数据
			 * `payment_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '支付类型，1、在线支付，2、货到付款',
			 `receiver_area_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人地区名称(省，市，县)街道',
			 `receiver_mobile` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人手机',
			 `receiver` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
			 */
			tbOrder.setPaymentType(order.getPaymentType());
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());
			tbOrder.setReceiverMobile(order.getReceiverMobile());
			tbOrder.setReceiver(order.getReceiver());

			double payment=0.00;
			//遍历购物车商品详情列表，组装订单详情数据
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			for (TbOrderItem orderItem : orderItemList) {
				//`id` bigint(20) NOT NULL,    //不是主键自增
				orderItem.setId(idWorker.nextId());
				//`order_id` bigint(20) NOT NULL COMMENT '订单id',
				orderItem.setOrderId(orderId);

				payment+=orderItem.getTotalFee().doubleValue();

				orderItemMapper.insert(orderItem);
			}
			totalMoney+=payment;
			//实付金额，后端统计
			tbOrder.setPayment(new BigDecimal(payment));
			//保存订单
			orderMapper.insert(tbOrder);
		}

		//如果是在线支付和保存一笔支付操作
		if(order.getPaymentType().equals("1")){
			TbPayLog payLog=new TbPayLog();
			/*	`out_trade_no` varchar(30) NOT NULL COMMENT '支付订单号',   //分布式存储 idWorker
		  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
		  `total_fee` bigint(20) DEFAULT NULL COMMENT '支付金额（分）',
		  `trade_state` varchar(1) DEFAULT NULL COMMENT '交易状态', //未支付状态
		  `user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
		  `order_list` varchar(200) DEFAULT NULL COMMENT '订单编号列表',  //一笔支付可能对应多笔订单  1,2,3
		  `pay_type` varchar(1) DEFAULT NULL COMMENT '支付类型',  //微信支付*/
			payLog.setOutTradeNo(idWorker.nextId()+"");
			payLog.setCreateTime(new Date());
			payLog.setTotalFee((long)(totalMoney*100));//0.01  1.0
			payLog.setTradeState("1");
			payLog.setUserId(order.getUserId());
			payLog.setOrderList(ids.toString().replace("[","").replace("]","").replace(" ",""));// 1,2,3
			payLog.setPayType("1");//微信支付
			payLogMapper.insert(payLog);

			//将支付日志保存到redis中
			redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
		}


		//清除redis中购物车列表数据
		redisTemplate.delete(order.getUserId());

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
