package com.lynp.ui.data;

import com.google.gson.annotations.SerializedName;

/**
 * item entry
 * Created by nmg on 16/1/28.
 */


public class RecommendEntry {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("photo")
    public String photo;
    @SerializedName("price")
    public float price;
    @SerializedName("mprice")
    public float mprice;
    @SerializedName("size")
    public float size;
}
