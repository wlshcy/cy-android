package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cong on 15/8/16.
 */
public class OrderEntry extends BaseEntry {
    @SerializedName("orderno")
    public String orderno;
    @SerializedName("alipay")
    public String alipay;
}
