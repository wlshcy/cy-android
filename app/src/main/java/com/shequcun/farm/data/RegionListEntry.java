package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegionListEntry extends BaseEntry {
	@SerializedName("regions")
	public List<RegionEntry> mList;
}
