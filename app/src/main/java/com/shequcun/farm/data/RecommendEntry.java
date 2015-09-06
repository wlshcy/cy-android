package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 推荐菜品
 * Created by apple on 15/8/18.
 */
public class RecommendEntry extends BaseEntry {
    //    id	int		自增主键	10
//    iid	int	菜品id		10
//    title	string	标题		宁夏枸杞
//    imgs	string	菜品图片		["http://f.hiphotos.baidu.com/image/pic/item/9213b07eca80653879a8611594dda144ad348272.jpg"]
//    price	int	价格	每份单价,单位：分	4000
//    packw	int	每份重量，单位：克		500
//    maxpacks	int	最大份数，0表示不限制	表示该菜品最多可以选择几份	500
//    amount	int	菜品总量，单位：克		500000
//    remains	int	剩余重量，单位：克		500000
//    created	int	添加时间	1970年1月1日至今的毫秒数	1423458933975
//    modified	int	修改时间	1970年1月1日至今的毫秒数
    @SerializedName("id")
    public int id;
    @SerializedName("iid")
    public int iid;
    @SerializedName("title")
    public String title;
    @SerializedName("imgs")
    public String[] imgs;
    @SerializedName("price")
    public int price;
    @SerializedName("packw")
    public int packw;
    @SerializedName("maxpacks")
    public int maxpacks;
    @SerializedName("amount")
    public int amount;
    @SerializedName("remains")
    public int remains;
    @SerializedName("descr")
    public String descr;
    @SerializedName("type")
    public int type;// 1.普通菜品，2.秒杀菜品
    @SerializedName("bought")//true表示已购买，不需要下单时再判断了
    public boolean bought;
//    "type": 2,
//    "iid": 17,
//            "maxpacks": 5,
//            "imgs": [
//            "https://img.shequcun.com/1508/14161/6ef8074e298e4c45ba9cc7940149e91a.png"
//            ],
//            "packw": 1000,
//            "title": "空心菜",
//            "id": 1,
//            "price": 10,
//            "descr": "",
//            "remains": 100000
}
