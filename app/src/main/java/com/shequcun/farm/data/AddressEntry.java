package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 地址信息(楼号+单元号+门牌号)
 * Created by apple on 15/8/11.
 */
public class AddressEntry extends BaseEntry {
    //    @SerializedName("buildingno")
//    public String buildingno;//楼号
//    @SerializedName("unitno")
//    public String unitno;//单元号
//    @SerializedName("houseno")
//    public String houseno;//门牌号
//    @SerializedName("address")
//    public String address;
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("mobile")
    public String mobile;
//    @SerializedName("city")
//    public String city;
//    @SerializedName("region")
//    public String region;
//    @SerializedName("street")
//    public String street;
    @SerializedName("zname")
    public String zname;
    @SerializedName("bur")
    public String bur;
//    @SerializedName("building")
//    public String building;//楼号
//    @SerializedName("unit")
//    public String unit;//单元号
//    @SerializedName("room")
//    public String room;//门牌号
//    @SerializedName("default")
    public boolean isDefault;

    public int zid=0;

//            "region": "朝阳区",
//            "street": "广渠路29号"
//            "zname": "九龙花园",
//            "building": "2",
//            "unit": "A座",
//            "room": "502",
//            "default" true


}
