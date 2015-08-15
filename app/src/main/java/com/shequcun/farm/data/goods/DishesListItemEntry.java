package com.shequcun.farm.data.goods;

import com.google.gson.annotations.SerializedName;
import com.shequcun.farm.data.BaseEntry;
import com.shequcun.farm.data.DishesItemEntry;

import java.util.List;

/**
 * Created by apple on 15/8/15.
 */
public class DishesListItemEntry extends BaseEntry {
    @SerializedName("items")
    public List<DishesItemEntry> aList;
}
