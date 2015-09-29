package com.shequcun.farm.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.shequcun.farm.R;

/**
 * Created by apple on 15/8/10.
 */
public class Constrants {
    public final static String Customer_Service_Phone = "01067789567";
    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_CHECK_FLAG = 2;
    public static final String URL_FARM = "http://store.shequcun.com/yc_farm/";
    public static final String URL_SHARE = "http://store.shequcun.com/yc_recom_item/";
    public static final String APP_ID = "wxedddf5c468bfd955";
    public static final DisplayImageOptions image_display_options_disc = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.grey_def_bg)
            .showImageForEmptyUri(R.drawable.grey_def_bg)
            .showImageOnFail(R.drawable.grey_def_bg).cacheOnDisc(true).build();
    public static final DisplayImageOptions image_display_options_cache = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.grey_def_bg)
            .showImageForEmptyUri(R.drawable.grey_def_bg)
            .showImageOnFail(R.drawable.grey_def_bg).cacheInMemory(true).build();
}
