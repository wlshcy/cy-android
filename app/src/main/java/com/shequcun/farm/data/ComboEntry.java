package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 套餐数据结构
 * Created by apple on 15/8/12.
 */
public class ComboEntry extends BaseEntry {
    @SerializedName("id")
    public int id;
    @SerializedName("fid")
    public int fid;//农庄id
    @SerializedName("title")
    public String title;//套餐标题
    @SerializedName("descr")
    public String descr;//介绍
    @SerializedName("img")
    public String img;//套餐图片
    @SerializedName("titles")
    public String[] tiles;
    @SerializedName("weights")
    public int[] weights;//每次配送的重量，单位：克
    @SerializedName("prices")
    public int[] prices;//套餐价格，单位：分
    @SerializedName("mprices")
    public int[] mprices;//市场价格	单位：分
    @SerializedName("shipday")
    public int[] shipday;//配送日	单位：日/周
    @SerializedName("source")
    public int source;
    @SerializedName("sales")
    public int sales;//套餐销量
    @SerializedName("status")
    public int status;//套餐销量
    @SerializedName("issue_no")
    public int issue_no;
    @SerializedName("duration")
    public int duration;
    @SerializedName("index")
    public int index;
    @SerializedName("wimgs")
    public String wimgs[];

//    tiles	[string]	展示图列表		["http://f.hiphotos.baidu.com/image/pic/item/9213b07eca80653879a8611594dda144ad348272.jpg"]
//    weights	[int]	重量	每次配送的重量，单位：克	[8000,10000]
//    prices	[int]	价格	套餐价格，单位：分	[199900,239900]
//    mprices	[int]	市场价格	单位：分	[219900,259900]
//    shipday	[int]	配送日	单位：日/周, [2]:每周2配送一次;[2,5]:每周2 与 周5 各配送一次	[2,5]
//    source	int	来源	1:线上;2:线下	1
//    sales	int	套餐销量		50
//    status	int	状态	1.上架; 2.下架	1
//    created	int	添加时间	1970年1月1日至今的毫秒数	1423458933975
//    modified	int	修改时间	1970年1月1日至今的毫秒数	1423458933975
//    year
}
