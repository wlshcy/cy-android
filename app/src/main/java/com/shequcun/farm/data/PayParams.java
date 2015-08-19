package com.shequcun.farm.data;

import java.io.Serializable;

/**
 * Created by apple on 15/8/19.
 */
public class PayParams implements Serializable {
    /**
     * 支付订单
     */
    public String orderno;
    /**
     * 支付金额,单位:分
     */
    public double orderMoney;
    /**
     * 支付宝订单内容
     */
    public String alipay;
    /**
     * 是否推荐菜品
     */
    public boolean isRecoDishes;
}
