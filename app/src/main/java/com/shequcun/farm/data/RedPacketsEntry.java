package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsEntry extends BaseEntry {
    @SerializedName("coupons")
    public List<CouponEntry> list;
}
