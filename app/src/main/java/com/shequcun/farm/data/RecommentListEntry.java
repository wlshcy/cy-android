package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;
import com.lynp.ui.data.RecommendEntry;

import java.util.List;

/**
 * Created by apple on 15/8/18.
 */
public class RecommentListEntry extends BaseEntry{
    @SerializedName("items")
    public List<RecommendEntry> aList;
}
