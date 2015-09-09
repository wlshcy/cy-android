package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cong on 15/9/8.
 */
public class CouponEntry {
    @SerializedName("id")
    public int id;
    @SerializedName("cpid")
    public int cpid;
    @SerializedName("uid")
    public int uid;
    @SerializedName("type")
    public int type;
    @SerializedName("par")
    public int par;
    @SerializedName("charge")
    public int charge;
    @SerializedName("used")
    public int used;
    @SerializedName("expire")
    public int expire;
    @SerializedName("created")
    public long created;
}
