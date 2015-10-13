package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 15/10/13.
 */
public class MyOrderDetailListEntry extends BaseEntry{
    @SerializedName("data")
    public AlreadyPurchasedListEntry aList;
    @SerializedName("cpflag")
    public boolean cpflag;
}
