package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mac on 15/9/7.
 */
public class LinkEntry implements Serializable {
    //    "type": 1,  // 1.套餐详情, 2.菜品详情
//            "id": 10,  // 套餐/菜品id
    @SerializedName("type")
    public int type;
    @SerializedName("id")
    public int id;
}
