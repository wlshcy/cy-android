package com.lynp.ui.data;

/**
 * Created by niuminguo on 16/3/29.
 */
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

/**
 * 地址信息(楼号+单元号+门牌号)
 * Created by apple on 15/8/11.
 */
public class AddressEntry extends BaseEntry {
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("city")
    public String city = "";
    @SerializedName("region")
    public String region = "";
    //    @SerializedName("zname")
//    public String zname;
//    @SerializedName("bur")
//    public String bur;
    @SerializedName("address")
    public String address;
    @SerializedName("default")
    public boolean isDefault;
    public int zid = 0;

}