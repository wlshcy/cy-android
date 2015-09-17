package com.shequcun.farm.data;

import java.io.Serializable;

/**
 * Created by mac on 15/9/12.
 */
public class OtherInfo implements Serializable {
    public String extras;//  选菜订单可选，单品订单必传，格式：id1:amount1,id2:amount2...
    public String memo;//备注
    public int type;
    public boolean isSckill = false;

    public int item_type=0;//1.选菜菜品 2.普通单品, 3.秒杀单品
}
