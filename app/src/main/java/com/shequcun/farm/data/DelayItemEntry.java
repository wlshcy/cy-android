package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cong on 15/10/8.
 */
public class DelayItemEntry {
    @SerializedName("orderno")
    public String orderno;
    @SerializedName("combo_id")
    public int combo_id;
    @SerializedName("title")
    public String title;
    @SerializedName("chosen")
    public boolean chosen;
    @SerializedName("delay")
    public DelayInfo delay;
}
