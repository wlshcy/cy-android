package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple check_turn_on 15/7/29.
 */
public class VersionEntry extends BaseEntry {
    @SerializedName("version")
    public String version;
    @SerializedName("change")
    public String change;
    @SerializedName("url")
    public String url;
    @SerializedName("status")
    public int status;
}