package com.shequcun.farm.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.common.widget.BadgeView;
import com.shequcun.farm.R;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by apple on 15/8/4.
 */
public class Utils {
    /**
     * 隐藏键盘
     *
     * @param context
     * @param layout
     */
    public static void hideVirtualKeyboard(Context context, View layout) {
        if (context == null || layout == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(layout.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 分转换为圆（保留两位小数）
     *
     * @param peney
     * @return
     */
    public static String unitPeneyToYuan(int peney) {
        if (peney <= 0)
            return "";
        double yuan = (double) peney / 100;
        DecimalFormat df = new DecimalFormat("###.00");
        return "￥" + (peney / 100 == 0 ? "0" + df.format(yuan) : df.format(yuan));
    }

    public static String unitPeneyToYuanEx(int peney) {
        double yuan = (double) peney / 100;
        DecimalFormat df = new DecimalFormat("###.00");
        return df.format(yuan) + "元";
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (view == null)
                continue;
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (params != null) {
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }

    }

    /**
     * 将克转换为千克
     *
     * @param g
     * @return
     */
    public static String unitConversion(int g) {
        String restDistanceStr;
        float fdis = ((float) g) / 500;
        fdis = (float) (Math.round(fdis * 10)) / 10;
        if (fdis * 10 % 10 == 0) {
            restDistanceStr = (int) fdis + "斤";
        } else {
            restDistanceStr = fdis + "斤";
        }
        return restDistanceStr;
//        String tmpStr = "";
//        if (g >= 1000) {
//            float fdis = ((float) g) / 1000;
//            fdis = (float) (Math.round(fdis * 10)) / 10;
//            if (fdis * 10 % 10 == 0) {
//                tmpStr = (int) fdis + "千克";
//            } else {
//                tmpStr = fdis + "千克";
//            }
//        } else {
//            tmpStr = g + "克";
//        }
//
//        return tmpStr;
    }

    public static SpannableString getSpanableSpan(String strFrom1,
                                                  String strTo1, int size1, int size2, int color1, int color2) {
        if (TextUtils.isEmpty(strFrom1) || TextUtils.isEmpty(strTo1))
            return null;

        SpannableString wordtoSpan = new SpannableString(strFrom1 + strTo1);

        int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
        int start = 0;
        int end = strFrom1.length();
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size1), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(color1), start, end,
                flag);

        start = end;
        end += strTo1.length();
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size2), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(color2), start, end,
                flag);
//        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                start, end, flag); // 粗体
        return wordtoSpan;
    }

    public static SpannableString getSpanableSpan(String strFrom1,
                                                  String strTo1, String strFrom2, int size1, int size2) {
        if (TextUtils.isEmpty(strFrom1) || TextUtils.isEmpty(strTo1)
                || TextUtils.isEmpty(strFrom2))
            return null;

        SpannableString wordtoSpan = new SpannableString(strFrom1 + strTo1
                + strFrom2);

        int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
        int start = 0;
        int end = strFrom1.length();
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size1), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(0xFF444444), start, end,
                flag);


        start = end;
        end += strTo1.length();
        wordtoSpan.setSpan(new AbsoluteSizeSpan(size2), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(0xFFFC3F30), start, end,
                flag);

//        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                start, end, flag); // 粗体

        start = end;
        end += strFrom2.length();

        wordtoSpan.setSpan(new AbsoluteSizeSpan(size1), start, end, flag);
        wordtoSpan.setSpan(new ForegroundColorSpan(0xFF444444), start, end,
                flag);
        return wordtoSpan;
    }

    public static String getTime(long d) {
        Date dat = new Date(d);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        String sb = format.format(gc.getTime());
        return sb;
    }

    public static String getMMdd(long d) {
        Date dat = new Date(d);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "MM月dd日");
        String sb = format.format(gc.getTime());
        return sb;
    }

    public static BadgeView buildBadgeView(Context mContext, ImageView mIv) {
        BadgeView mBadgeViewShopCart = new BadgeView(mContext, mIv);
        mBadgeViewShopCart.setWidth(ResUtil.dip2px(mContext, 20));
        mBadgeViewShopCart.setHeight(ResUtil.dip2px(mContext, 20));
        mBadgeViewShopCart.setBackgroundResource(R.drawable.red_oval);
        mBadgeViewShopCart.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.dip2px(mContext, 10));
        return mBadgeViewShopCart;
    }
}
