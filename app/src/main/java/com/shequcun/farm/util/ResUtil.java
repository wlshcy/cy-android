package com.shequcun.farm.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by apple on 15/8/8.
 */
public class ResUtil {
    public static int dipToPixel(Context context, int dipValue) {
        if (context == null) {
            return dipValue; // 原值返回
        }
        try {
            float pixelFloat = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dipValue, context
                            .getResources().getDisplayMetrics());
            return (int) pixelFloat;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dipValue;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
