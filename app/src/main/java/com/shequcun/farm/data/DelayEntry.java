package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cong on 15/9/7.
 */
public class DelayEntry extends BaseEntry{
    @SerializedName("delayed")
    public boolean delayed;
    @SerializedName("date")
    public long date;
    @SerializedName("times")
    public int times;
    @SerializedName("data")
    public List<DelayItemEntry> data;
}
