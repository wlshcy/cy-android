package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 固定套餐
 * Created by apple on 15/8/17.
 */
public class FixedComboEntry {
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
    @SerializedName("ciid")
    public int ciid;
    @SerializedName("remains")
    public int remains;
    @SerializedName("amount")
    public int amount;
    @SerializedName("imgs")
    public String imgs[];

    public int count;
}
