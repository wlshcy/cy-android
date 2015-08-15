package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/8/13.
 */
public class AddressListEntry extends BaseEntry {
    @SerializedName("addresses")
    public List<AddressEntry> aList;
}
