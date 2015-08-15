package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ZoneListEntry extends BaseEntry {
    @SerializedName("zones")
    public List<ZoneEntry> aList;
}
