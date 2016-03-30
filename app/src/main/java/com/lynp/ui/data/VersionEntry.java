package com.lynp.ui.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by niuminguo on 16/3/29.
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