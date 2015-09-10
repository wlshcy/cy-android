package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cong on 15/9/9.
 */
public class CouponShareEntry extends BaseEntry{
    @SerializedName("url")
    public String url;
    @SerializedName("content")
    public String content;
    @SerializedName("title")
    public String title;
}
