package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cong on 15/9/18.
 */
public class MyComboEntry extends BaseEntry {
    @SerializedName("combo_orders")
    public List<ComboEntry> combos;
}
