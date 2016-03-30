package com.lynp.ui.data;

/**
 * Created by niuminguo on 16/3/29.
 */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseEntry implements Serializable {
    @SerializedName("errcode")
    public String errcode;
    @SerializedName("errmsg")
    public String errmsg;
}