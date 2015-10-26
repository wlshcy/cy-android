package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 菜品选择表
 * Created by apple on 15/8/15.
 */
public class DishesItemEntry extends BaseEntry {
    @SerializedName("id")
    public int id;
    @SerializedName("combo_id")
    public int combo_id;
    @SerializedName("issue_no")
    public int issue_no;
    @SerializedName("iid")
    public int iid;
    @SerializedName("amount")
    public int amount;//菜品数量
    @SerializedName("packw")
    public int packw;//每份重量 单位：克 500
    @SerializedName("maxpacks")
    public int maxpacks;//最大份数，0 ,表示不限制 表示该菜品最多可以选择几份
    @SerializedName("title")
    public String title;
    @SerializedName("remains")
    public int remains;//剩余
    @SerializedName("imgs")
    public String imgs[];
    /*份数*/
    private int count;


    public void setCount(int count) {
        this.count = count;
    }

//    public float getTotalPrice() {
//        return count * price;
//    }

    public int getCount() {
        return count;
    }

//    @SerializedName("weights")
//    public int weights;
//    @SerializedName("price")
//    public int price;
//    @SerializedName("category")
//    public int category;
//    @SerializedName("type")
//    public int type;
//    id	int		自增主键	10
//    combo_id	int	套餐id	表示是属于哪个套餐的	10
//    issue_no	int	期数	表示是第几期的菜品	10
//    iid	int	菜品id		10
//    title	string	标题		宁夏枸杞
//    img string
//    菜品首图 http
//    ://f.hiphotos.baidu.com/image/pic/item/9213b07eca80653879a8611594dda144ad348272.jpg
    // packw
//    int 每份重量
//    ，单位：克 500
//    maxpacks
//    int 最大份数
//    ，0
//    表示不限制 表示该菜品最多可以选择几份
//    500
//    amount
//    int 菜品总量
//    ，单位：克 500000
//    remains
//    int 剩余重量
//    ，单位：克 500000
//    created
//    int 添加时间
//    1970年1月1日至今的毫秒数 1423458933975
//    modified
//    int 修改时间
//    1970年1月1日至今的毫秒数 1423458933975

}
