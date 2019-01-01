package com.pingyougou.pay.service;

import com.pingyougou.pojo.TbPayLog;


import java.util.Map;

public interface PayService {

    /**
     * 调用统一下单接口，生成二维码
     * out_trade_no:订单号
     * total_fee：支付金额
     */
    public Map<String,Object> createNative(String out_trade_no,String total_fee) throws Exception;

    /**
     * 支付状态查询
     * out_trade_no:订单号
     */
    public Map queryPayStatus(String out_trade_no) throws Exception;

    TbPayLog selectPayLogFromRedis(String userId);

    void updateStatus(String out_trade_no, String transaction_id);
}
