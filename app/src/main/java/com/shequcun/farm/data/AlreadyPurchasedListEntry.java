package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/8/20.
 */
public class AlreadyPurchasedListEntry extends BaseEntry{
    @SerializedName("items")
    public List<AlreadyPurchasedEntry> aList;
}
