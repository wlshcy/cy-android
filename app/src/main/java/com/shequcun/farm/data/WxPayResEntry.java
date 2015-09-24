package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 15/9/21.
 */
public class WxPayResEntry {
    public String appId;
    @SerializedName("partnerid")
    public String partnerid;
    @SerializedName("prepayid")
    public String prepayid;
    @SerializedName("package")
    public String packagestr;
    @SerializedName("noncestr")
    public String noncestr;
    @SerializedName("timestamp")
    public String timestamp;
    @SerializedName("sign")
    public String sign;
}
