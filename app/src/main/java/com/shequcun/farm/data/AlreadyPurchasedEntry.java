package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 已下单数据表
 * Created by apple on 15/8/20.
 */
public class AlreadyPurchasedEntry {
    //    id	int		自增主键	10
//    orderno	int	订单号	1537749250347349
//    title	string	标题		宁夏枸杞
//    img	string	菜品首图		http://f.hiphotos.baidu.com/image/pic/item/9213b07eca80653879a8611594dda144ad348272.jpg
//    packw	int	每份重量，单位：克		500
//    packs	int	菜品份数		2
    @SerializedName("id")
    public int id;
    @SerializedName("orderno")
    public String orderno;
    @SerializedName("title")
    public String title;
    @SerializedName("img")
    public String img;
    @SerializedName("packw")
    public int packw;
    @SerializedName("packs")
    public int packs;//已购买菜品份数
}
