package com.shequcun.farm.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 15/9/18.
 */
public class MyComboOrder extends BaseEntry {
    //    {
//        "combo_orders":
//        [
//        {
//            "chgtime":
//            {
//                "0": 1440044393886,
//                    "1": 1440044393886,
//                    "2": 1440380859879,
//                    "3": 1440380872196
//            },
//            "title": "天居园199特价月套餐",
//                "con": 1593632920345974,  // 套餐订单号
//                "status": 3  // 1.待配送, 2.配送中, 3.配送中, 5.套餐配送完成
//        }
//        ]
//    }
    @SerializedName("chgtime")
    public JsonObject chgtime;
    @SerializedName("title")
    public String title;
    @SerializedName("con")
    public String con;
    @SerializedName("status")
    public int status;
    @SerializedName("shipday")
    public int[] shipday;//配送日	单位：日/周
    @SerializedName("times")
    public int times;
    @SerializedName("duration")
    public int duration;

}
