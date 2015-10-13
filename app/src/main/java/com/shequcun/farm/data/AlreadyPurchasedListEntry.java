package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/8/20.
 */
public class AlreadyPurchasedListEntry extends BaseEntry {
    @SerializedName("items")
    public List<AlreadyPurchasedEntry> aList;
    @SerializedName("cpflag")
    public boolean cpflag;
    @SerializedName("spares")
    public List<AlreadyPurchasedEntry> dIe;
    @SerializedName("fid")
    public int fid;
    @SerializedName("farm")
    public String farm;
//    "fid": 2,
//            "farm": "无忧无虑",
//            "status": 3,
}
