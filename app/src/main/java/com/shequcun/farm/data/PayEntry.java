package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 15/9/23.
 */
public class PayEntry extends BaseEntry {
    //    "fee":55500,
//            "alipay":"partner="2088911366083289"&seller_id="liushouchang@shequcun.com"&out_trade_no="1586456929303190"&subject="有菜555月套餐"&body="有菜订单"&total_fee="555.00"¬ify_url="https://api.shequcun.com/alipay/notify?apptype=5&extra="&service="mobile.securitypay.pay"&payment_type="1"&_input_charset="utf-8"&it_b_pay="30m"&show_url="m.alipay.com"&sign="38vLrsERjubSJfkgzEfJZqaev8Bl4wa2zG6rEX%2BG3uyUJX6vt1mRLmhpjI2Kz%2F4fancwKmXqYQramqQhjtN28VpmW5Pm0QMCo%2FGUeBwwncqY44V4qI7JACvQJkWwoU2VMSu0O%2B8%2F%2BRaodThD4%2FZFphTPrZTPTcY9iRd4mWapICM%3D"&sign_type="RSA"",
//            "freight":0,
//            "orderno":1586456929303190
    @SerializedName("fee")
    public int fee;
//    @SerializedName("alipay")
    public String alipay;
    @SerializedName("freight")
    public int freight;
    @SerializedName("orderno")
    public String orderno;
    @SerializedName("wxpay")
    public WxPayResEntry wxpay;
}
