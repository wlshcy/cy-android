package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsEntry extends BaseEntry {
    @SerializedName("coupons")
    public ArrayList<CouponEntry> list;
}