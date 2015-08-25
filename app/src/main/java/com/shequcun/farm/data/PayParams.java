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
    public int orderMoney;
    /**
     * 支付宝订单内容
     */
    public String alipay;
    /**
     * 是否推荐菜品
     */
    public boolean isRecoDishes;
    /**
     * 最后流程显示内容
     */
    public int titleId;

    public void setParams(String orderno, int orderMoney, String alipay, boolean isRecoDishes, int titleId) {
        this.orderno = orderno;
        this.orderMoney = orderMoney;
        this.alipay = alipay;
        this.isRecoDishes = isRecoDishes;
        this.titleId = titleId;
    }
}
