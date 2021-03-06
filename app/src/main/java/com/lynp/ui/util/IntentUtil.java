package com.lynp.ui.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by apple on 15/8/8.
 */
public class IntentUtil {

    public final static String UPDATE_COMBO_PAGE = "com.youcai.refresh.combo";
    public final static String UPDATE_MINE_PAGE = "com.youcai.refresh";
    public final static String UPDATE_ADDRESS_REQUEST = "com.youcai.refresh.address_request";

    /**
     * 发送刷新套餐页的广播
     *
     * @param mContext
     */
    public static void sendUpdateComboMsg(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_COMBO_PAGE);
        mContext.sendBroadcast(intent);
    }

    public static void sendUpdateMyInfoMsg(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_MINE_PAGE);
        mContext.sendBroadcast(intent);
    }


    /**
     * 刷新购物车界面
     */
    public static final String UPDATE_SHOPPING_CART_MSG = "refresh_shopping_cart";

    public static void sendUpdateShoppingCartMsg(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_SHOPPING_CART_MSG);
        mContext.sendBroadcast(intent);
    }

    public static void sendUpdateAddressRequest(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_ADDRESS_REQUEST);
        mContext.sendBroadcast(intent);
    }


    public static final String UPDATE_WX_PAY_RESULT_MSG = "com.youcai.refresh.pay.result";

    public static void sendWxPayResultMsg(Context mContext, int payCode) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_WX_PAY_RESULT_MSG);
        intent.putExtra("PayCode", payCode);
        mContext.sendBroadcast(intent);
    }
}
