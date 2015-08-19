package com.shequcun.farm.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 15/8/18.
 */
public class HistoryOrderEntry {


    //    "status": 1,
//            "combo_idx": 1,
//            "img": "https://img.shequcun.com/1508/15111/ffa250f3802a4124b0682bee7578842e.png",
//            "id": 1,
//            "cod": false,
//            "times": 1,
//            "title": "基本蔬菜套餐",
//            "address": "九龙花园",
//            "year": 2015,
//            "issue_no": 1,
//            "combo_id": 1,
//            "orderno": 1537749250347349,
//            "uid": 3,
//            "type": 1,
//            "name": "公公",
//            "price": 832000,
//            "mobile": "14012345678",
    @SerializedName("status")
    public int status;//0.未付款, 1.待配送, 2.配送中, 3.配送完成, 4.取消订单
    @SerializedName("times")
    public int times;
//    @SerializedName("combo_idx")
//    public String combo_idx;
    @SerializedName("id")
    public int id;
    @SerializedName("type")
    public int type;
    @SerializedName("year")
    public int year;
//    @SerializedName("uid")
//    public int uid;
//    @SerializedName("combo_id")
//    public int combo_id;
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
}
