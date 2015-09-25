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

    public void setParams(int id, String orderno, int type, int combo_id, int price, String combo_idx, int status, String date, String name, String mobile, String address) {
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

}
