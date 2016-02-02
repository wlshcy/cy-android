package com.lynp.ui.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
/**
 * Created by niuminguo on 16/1/29.
 */

public class ItemDetailEntry{
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("desc")
    public String desc;
    @SerializedName("photo")
    public String photo;
    @SerializedName("price")
    public float price;
    @SerializedName("mprice")
    public float mprice;
    @SerializedName("size")
    public float size;
    @SerializedName("origin")
    public String origin;

    @SerializedName("count")
    public int count = 0;
}
