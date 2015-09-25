package com.shequcun.farm.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by apple on 15/8/18.
 */
public class HistoryOrderEntry implements Serializable {

    @SerializedName("status")
    public int status;//0.未付款, 1.待配送, 2.配送中, 3.配送完成, 4.取消订单
    @SerializedName("times")
    public int times;
    @SerializedName("combo_idx")
    public String combo_idx;

    //  combo_id	int	套餐id		10
    //   combo_idx	int	子套餐序号
    @SerializedName("id")
    public int id;
    @SerializedName("type")
    public int type;//1.套餐订单 2.选菜订单, 3.单品订单, 4.自动选菜订单
    @SerializedName("year")
    public int year;
    //    @SerializedName("uid")
//    public int uid;
    @SerializedName("combo_id")
    public int combo_id;
    //    @SerializedName("cod")
//    public boolean cod;
    @SerializedName("title")
    public String title;
    @SerializedName("address")
    public String address;
    @SerializedName("orderno")
    public String orderno;
    @SerializedName("name")
    public String name;
    @SerializedName("chgtime")
    public JsonObject json;
    @SerializedName("price")
    public int price;
    @SerializedName("item_type")
    public int item_type;//	int	菜品类型	1.选菜菜品 2.普通单品, 3.秒杀单品
    @SerializedName("mobile")
    public String mobile;
    public String date;
}
