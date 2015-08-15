package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/8/12.
 */
public class ComboListEntry extends BaseEntry {
    @SerializedName("mycombos")
    public List<ComboEntry> myCombos;//我的套餐
    @SerializedName("combos")
    public List<ComboEntry> aList;
}
