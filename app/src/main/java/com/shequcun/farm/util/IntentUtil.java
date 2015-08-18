package com.shequcun.farm.util;

import android.content.Context;
import android.content.Intent;

import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.ZoneEntry;

/**
 * Created by apple on 15/8/8.
 */
public class IntentUtil {

    public final static String UPDATE_COMBO_PAGE = "com.youcai.refresh。combo";
    public final static String UPDATE_MINE_PAGE = "com.youcai.refresh";

    /**
     * @param mContext
     */
    public static void sendUpdateMyInfoMsg(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_MINE_PAGE);
        intent.putExtra("UpdateAction", "UpdateMyInfo");
        mContext.sendBroadcast(intent);
    }

    /**
     * 发送刷新套餐页的广播
     *
     * @param mContext
     */
    public static void sendUpdateComboMsg(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_COMBO_PAGE);
//        intent.putExtra("UpdateAction", "UpdateMyInfo");
        mContext.sendBroadcast(intent);
    }

    public static void sendUpdateMyInfoMsg(Context mContext, ComboEntry entry) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_MINE_PAGE);
        intent.putExtra("ComboEntry", entry);
        mContext.sendBroadcast(intent);
    }

    public static void sendUpdateMyAddressMsg(Context mContext, ZoneEntry zEntry) {
        Intent intent = new Intent();
        intent.setAction("com.youcai.refresh.myaddress");
        intent.putExtra("ZoneEntry", zEntry);
        mContext.sendBroadcast(intent);
    }

    public static void sendUpdateMyAddressMsg(Context mContext, String community_name, String details_address) {
        Intent intent = new Intent();
        intent.setAction("com.youcai.refresh.myaddress");
//        intent.putExtra("ZoneEntry",zEntry);
        intent.putExtra("community_name", community_name);
        intent.putExtra("details_address", details_address);
        mContext.sendBroadcast(intent);
    }

    public static final String UPDATE_ORDER_DETAILS_MSG = "com.youcai.refresh.orderdetails.address";

    public static void sendUpdateOrderDetailsAddressMsg(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_ORDER_DETAILS_MSG);
//        intent.putExtra("AddressEntry", entry);
        mContext.sendBroadcast(intent);
    }

}
