package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 农庄表
 * Created by apple on 15/8/15.
 */
public class FarmEntry extends BaseEntry {

//    id	int	农庄id	自增主键	10
//    cid	int	城市id	参见区域表	1
//    city	string	城市	北京
//    rid	int	区域id(区/县级)	参见区域表	2
//    region	string	区域名称		昌平区
//    name	string	农庄名称		天下第一庄
//    address	string	农庄地址		陵镇下口村
//    contact	string	联系人		朱重八
//    phone	string	联系电话		82357812
//    descr	string	介绍		特色菜：秋葵
//    geom	GeoJSON	地理位置		{"type": "Point", "coordinates": [116.4190851, 39.8662988]}
//    created	int	添加时间	1970年1月1日至今的毫秒数	1423458933975
//    modified	int	修改时间	1970年1月1日至今的毫秒数	1423458933975

    @SerializedName("id")
    public int id;
    @SerializedName("cid")
    public int cid;
    @SerializedName("rid")
    public int rid;
    @SerializedName("region")
    public String region;
    @SerializedName("name")
    public String name;
    @SerializedName("address")
    public String address;
    @SerializedName("contact")
    public String contact;
    @SerializedName("phone")
    public String phone;
    @SerializedName("descr")
    public String descr;

}
