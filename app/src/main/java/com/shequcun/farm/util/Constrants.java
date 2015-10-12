package com.shequcun.farm.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.shequcun.farm.R;

/**
 * Created by apple on 15/8/10.
 */
public class Constrants {
    public final static String Customer_Service_Phone = "010-67789567";
    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_CHECK_FLAG = 2;
    public static final String URL_FARM = "http://store.shequcun.com/yc_farm/";
    public static final String URL_SHARE = "http://store.shequcun.com/yc_recom_item/";
    public static final String APP_ID = "wxedddf5c468bfd955";
    public static final BitmapDisplayer displayer = new FadeInBitmapDisplayer(0) {
        @Override
        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
            if (loadedFrom != LoadedFrom.MEMORY_CACHE) {
                super.display(bitmap, imageAware, loadedFrom);
            } else {
                imageAware.setImageBitmap(bitmap);
            }
        }
    };
    public static final DisplayImageOptions image_display_options_disc = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.grey_def_bg)
            .showImageForEmptyUri(R.drawable.grey_def_bg)
            .showImageOnFail(R.drawable.grey_def_bg).cacheOnDisc(true).build();
    public static final DisplayImageOptions image_display_options_cache = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.grey_def_bg)
            .showImageForEmptyUri(R.drawable.grey_def_bg)
            .resetViewBeforeLoading(true)
            .displayer(displayer)
            .cacheOnDisc(true)
            .showImageOnFail(R.drawable.grey_def_bg).cacheInMemory(true).build();
}
