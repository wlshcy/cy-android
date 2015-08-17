package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 固定套餐
 * Created by apple on 15/8/17.
 */
public class FixedListComboEntry extends BaseEntry{
    @SerializedName("combo_items")
    public List<FixedComboEntry> aList;
}
