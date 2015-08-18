package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/8/18.
 */
public class SlidesListEntry extends BaseEntry{
    @SerializedName("slides")
    public List<SlidesEntry> aList;
}
