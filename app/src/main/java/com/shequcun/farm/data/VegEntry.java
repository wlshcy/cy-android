package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;
import com.lynp.ui.data.RecommendEntry;

import java.util.List;

/**
 * Created by mac on 15/9/7.
 */
public class VegEntry {
    @SerializedName("items")
    public List<RecommendEntry> items;//推荐菜品
}
