package com.lynp.ui.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by niuminguo on 16/2/1.
 */
public class OrderEnsureEntry implements Serializable{
    @SerializedName("items")
    public List<ItemDetailEntry> items;
    @SerializedName("price")
    public Float price;
    @SerializedName("freight")
    public Integer freight;
}
