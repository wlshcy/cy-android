package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

public class RegionEntry {
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("isleaf")
    public boolean isleaf;
}
