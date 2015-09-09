package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 15/9/7.
 */
public class HomeEntry extends BaseEntry {
    @SerializedName("mycombo")
    public ComboEntry myCombos;//我的套餐
    @SerializedName("items")
    public List<RecommendEntry> rList;//推荐菜品
    @SerializedName("slides")
    public List<SlidesEntry> sList;//轮播图
}
