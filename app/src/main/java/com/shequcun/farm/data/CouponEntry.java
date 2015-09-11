package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by cong on 15/9/8.
 * {"coupons":[{"uid":201,"used":false,"charge":5000,"expire":1444380774525,"discount":650,"cpid":1,"id":6,"type":2,"created":1441788774525,"distype":1}]}
 */
public class CouponEntry implements Serializable{
    @SerializedName("id")
    public int id;
    @SerializedName("cpid")
    public int cpid;
    @SerializedName("uid")
    public int uid;
    @SerializedName("type")
    public int type;
    @SerializedName("charge")
    public int charge;
    @SerializedName("used")
    public boolean used;
    @SerializedName("expire")
    public long expire;
//    @SerializedName("created")
//    public long created;
    @SerializedName("discount")
    public int discount;
    @SerializedName("distype")
    public int distype;
}
