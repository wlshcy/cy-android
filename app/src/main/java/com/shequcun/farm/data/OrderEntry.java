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
    public String orderno;//订单号
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
    @SerializedName("wxpay")
    public WxPayResEntry wxpay;
    @SerializedName("last")
    public boolean last;
//    "fee":59800,
//            "orderno":1587496832633814,
//            "freight":0,
//            "wxpay":{
//        "partnerid":"1269547101",
//                "noncestr":"d795152df98f423aaa417bcb9f49b5d5",
//                "sign":"2BAF1E66D57F41A9B623F328907456EC",
//                "prepayid":"wx20150923193503d0c85683ae0027600808",
//                "package":"Sign=WXPay",
//                "timestamp":1443008104
//    }
}
