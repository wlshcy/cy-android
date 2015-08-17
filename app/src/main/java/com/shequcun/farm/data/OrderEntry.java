package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * 订单表
 * Created by cong on 15/8/16.
 */
public class OrderEntry extends BaseEntry {
    @SerializedName("alipay")
    public String alipay;
    @SerializedName("id")
    public int id;
    @SerializedName("orderno")
    public int orderno;//订单号
    @SerializedName("uid")
    public int uid;//用户ID
    @SerializedName("combo_id")
    public int combo_id;
    @SerializedName("combo_idx")
    public int combo_idx;
    @SerializedName("title")
    public String title;
    @SerializedName("img")
    public String img;
    @SerializedName("name")
    public String name;//户名称	订单要配送的用户名称	张三
    @SerializedName("issue_no")
    public int issue_no;//期数
    @SerializedName("type")
    public int type;//订单类型	1.套餐订单 2.选菜订单, 3.单品订单, 4.自动选菜订单
    @SerializedName("price")
    public int price;
    @SerializedName("times")
    public int times;
    //    id	int	订单id	自增主键	10
//    orderno	int	订单号	1537749250347349
//    uid	int	用户ID	用户的ID	100
//    combo_id	int	套餐id		10
//    combo_idx	int	子套餐序号		2
//    title	string	订单标题		鸡蛋套餐
//    img	string	订单图片		https://img.shequcun.com/1508/14171/5d0cbc4ff61e4b188b4410096b0a088c.png
//    year	int	年份		2015
//    issue_no	int	期数	套餐/单品订单的期数为0	10
//    type	int	订单类型	1.套餐订单 2.选菜订单, 3.单品订单, 4.自动选菜订单	1
//    price	int	订单价格，单位：分		0
//    times	int	第n次配送	表示选菜订单或自动选菜订单的配送次数	12
//    name	string	用户名称	订单要配送的用户名称	张三
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("address")
    public String address;
    //    mobile	string	手机号码	订单要配送的用户手机号	13854385438
//    address	string	配送地址		北京市朝阳区九龙花园2号楼A座502
    @SerializedName("cod")
    public boolean cod;
    //    cod	bool	货到付款		true
    @SerializedName("status")
    public int status;
    //    status	int	订单状态	0.未付款, 1.待配送, 2.配送中, 3.配送完成, 4.取消订单	1
//    chgtime	json	状态变更时间	1970年1月1日至今的毫秒数	{"0":1420819200000,"1":1423458933975}
    @SerializedName("created")
    public long created;//	int	添加时间	1970年1月1日至今的毫秒数	1423458933975
    @SerializedName("modified")
    public long modified;//int	修改时间	1970年1月1日至今的毫秒数	1423458933975
}
