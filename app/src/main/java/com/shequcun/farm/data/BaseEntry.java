package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseEntry implements Serializable {
    @SerializedName("errcode")
    public String errcode;
    @SerializedName("errmsg")
    public String errmsg;
}
