package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/8/17.
 */
public class OrderListEntry extends BaseEntry{
    @SerializedName("orders")
    public List<HistoryOrderEntry> aList;
}
