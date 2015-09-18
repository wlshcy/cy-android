package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 15/9/18.
 */
public class MyComboOrderListEntry {
    @SerializedName("combo_orders")
    public List<MyComboOrder> aList;
}
