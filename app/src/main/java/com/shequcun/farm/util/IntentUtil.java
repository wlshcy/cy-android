package com.shequcun.farm.util;

import android.content.Context;
import android.content.Intent;

import com.shequcun.farm.data.ZoneEntry;

/**
 * Created by apple on 15/8/8.
 */
public class IntentUtil {
    /**
     * @param mContext
     */
    public static void sendUpdateMyInfoMsg(Context mContext) {
        Intent intent = new Intent();
        intent.setAction("com.youcai.refresh");
        intent.putExtra("UpdateAction", "UpdateMyInfo");
        mContext.sendBroadcast(intent);
    }

    public  static void  sendUpdateMyAddressMsg(Context mContext,ZoneEntry zEntry){
        Intent intent = new Intent();
        intent.setAction("com.youcai.refresh.myaddress");
        intent.putExtra("ZoneEntry",zEntry);
        mContext.sendBroadcast(intent);
    }

    public static void sendUpdateMyAddressMsg(Context mContext,String community_name,String details_address){
        Intent intent = new Intent();
        intent.setAction("com.youcai.refresh.myaddress");
//        intent.putExtra("ZoneEntry",zEntry);
        intent.putExtra("community_name",community_name);
        intent.putExtra("details_address",details_address);
        mContext.sendBroadcast(intent);
    }

}
