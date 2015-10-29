package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/8/17.
 */
public class OrderListEntry extends BaseEntry {
    @SerializedName("orders")
    public List<HistoryOrderEntry> aList;
    @SerializedName("addon")
    public java.util.ArrayList<FixedComboEntry> fList;
//    "addon":  // 套餐固定菜品
//            [
//    {
//        "id": 1,
//            "title": "鸡蛋",
//            "imgs": ["https://img.shequcun.com/1508/15111/ffa250f3802a4124b0682bee7578842e.png"],
//        "quantity": 10,
//            "unit": "枚",
//            "amount": 1
//    }
//    ]
}
