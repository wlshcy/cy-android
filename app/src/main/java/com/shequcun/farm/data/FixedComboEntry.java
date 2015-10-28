package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 固定套餐
 * Created by apple on 15/8/17.
 */
public class FixedComboEntry {
    //    "quantity": 10,
//            "id": 39,
//            "combo_id": 1,
//            "freq": 2,
//            "title": "柴鸡蛋",
//            "create": 1439538006796,
//            "iid": 20,
//            "img": "https://img.shequcun.com/1508/14151/21bac5caa3e6417cb5cd00e48ffa50d1.png",
//            "unit": "个"
    @SerializedName("quantity")
    public int quantity;//表示每次配送的菜品数量
    @SerializedName("id")
    public int id;
    @SerializedName("combo_id")
    public int combo_id;
    @SerializedName("freq")//表示几周配送一次
    public int freq;
    @SerializedName("title")
    public String title;
    @SerializedName("img")
    public String img;
    @SerializedName("unit")
    public String unit;//个
    @SerializedName("iid")
    public int iid;
    @SerializedName("remains")
    public int remains;

    @SerializedName("imgs")
    public String imgs[];
}
