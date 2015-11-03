package com.shequcun.farm.data;

import java.io.Serializable;

/**
 * Created by apple on 15/8/21.
 */
public class ModifyOrderParams implements Serializable {

    public int id;
    public String orderno;
    public int type;
    public int combo_id;
    public int price;
    public String combo_idx;
    public int allWeight;
    public String date;
    public int status;
    public int order_type;

    public int shipday[];
    public int times;
    public int duration;
    public String con;
    public int reason;

    public void setParams(int id, String orderno, int type, int combo_id, int price, String combo_idx, int status, String date, String name, String mobile, String address, int order_type, String placeAnOrderDate, java.util.ArrayList<FixedComboEntry> fList, int shipday[], int times, String con, int duration) {
        this.id = id;
        this.orderno = orderno;
        this.type = type;
        this.combo_id = combo_id;
        this.price = price;
        this.combo_idx = combo_idx;
        this.status = status;
        this.date = date;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.order_type = order_type;
        this.placeAnOrderDate = placeAnOrderDate;
        this.fList = fList;
        this.shipday = shipday;
        this.times = times;
        this.con = con;
        this.duration = duration;
    }

    /**
     * 收货人姓名
     */
    public String name;
    /**
     * 收货人手机号
     */
    public String mobile;
    /**
     * 收货人地址
     */
    public String address;

    /***
     * 下单日期
     */
    public String placeAnOrderDate;

    /**
     * 套餐搭配菜品
     */
    public java.util.ArrayList<FixedComboEntry> fList;
}
