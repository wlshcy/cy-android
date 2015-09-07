package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 套餐详情
 * Created by mac on 15/9/7.
 */
public class ComboDetailEntry extends BaseEntry {
    @SerializedName("combo")
    public ComboEntry combo;
}
