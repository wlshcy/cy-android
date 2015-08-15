package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

public class ZoneEntry extends BaseEntry {
	@SerializedName("id")
	public int id;
	@SerializedName("cid")
	public int cid;
	@SerializedName("city")
	public String city;
	@SerializedName("rid")
	public int rid;
	@SerializedName("region")
	public String region;
	@SerializedName("name")
	public String name;
	@SerializedName("address")
	public String address;
	@SerializedName("bgimg")
	public String bgimg;
	@SerializedName("lat")
	public double lat;
	@SerializedName("lng")
	public double lng;
	@SerializedName("ozid")
	public int ozid;
	@SerializedName("status")
	public int status = -1;
	@SerializedName("dist")
	public int dist;
	@SerializedName("phone")
	public String phone;
}