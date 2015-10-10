package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 推荐菜品
 * Created by apple on 15/8/18.
 */
public class RecommendEntry extends BaseEntry {
    @SerializedName("id")
    public int id;
    @SerializedName("iid")
    public int iid;
    @SerializedName("title")
    public String title;
    @SerializedName("imgs")
    public String[] imgs;
    @SerializedName("price")
    public int price;
    @SerializedName("packw")
    public int packw;
    @SerializedName("maxpacks")
    public int maxpacks;
    @SerializedName("amount")
    public int amount;
    @SerializedName("remains")
    public int remains;
    @SerializedName("descr")
    public String descr;
    @SerializedName("type")
    public int type;// 1.普通菜品，2.秒杀菜品
    @SerializedName("bought")//true表示已购买，不需要下单时再判断了
    public boolean bought;
    @SerializedName("sales")
    public int sales;//销量
    @SerializedName("mprice")
    public int mprice;//市场价格
    @SerializedName("farm")
    public String farm;
    @SerializedName("fid")
    public int fid;
    @SerializedName("detail")
    public RecommendDetailEntry detail;

    /**
     * 购买单品的数量
     */
    @SerializedName("count")
    public int count = 0;

    public boolean isShowDtlFooter;
}
