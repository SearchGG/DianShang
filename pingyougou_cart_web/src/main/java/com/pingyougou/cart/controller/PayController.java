package com.pingyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.pay.service.PayService;
import com.pingyougou.pojo.TbPayLog;
import com.pingyougou.utils.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    /**
     * 生成二维码
     */
    @RequestMapping("/createNative")
    public Map<String, Object> createNative() {

        try {
            //获取当前登录用户
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            TbPayLog payLog = payService.selectPayLogFromRedis(userId);
            return payService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();//{}
        }

    }

    /**
     * 查询支付状态
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        try {

            int count = 0;

            while (true) {
                //每隔3秒调用一次
                Thread.sleep(3000);

                //超过5分钟，跳转循环（支付超时）
                count++;
                if (count > 100) {
                    return new Result(false, "timeout");
                }

                Map resultMap = payService.queryPayStatus(out_trade_no);
                //判断交易状态
                if (resultMap.get("trade_state").equals("SUCCESS")) {

                    //支付成功后，更新订单和支付日志状态
                    payService.updateStatus(out_trade_no, (String) resultMap.get("transaction_id"));


                    //支付成功
                    return new Result(true, "支付成功");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "支付失败");
        }
    }

}
