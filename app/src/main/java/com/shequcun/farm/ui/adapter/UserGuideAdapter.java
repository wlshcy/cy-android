package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

//import com.shequcun.farm.R;
import com.lynp.R;
/**
 * Created by mac on 15/10/14.
 */
public class UserGuideAdapter extends BaseAdapter {

    private int[] pics = {R.drawable.guide1, R.drawable.guide2,
            R.drawable.guide3, R.color.transparent_8000};
    private Context mContext;

    public UserGuideAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return pics.length;
    }

    @Override
    public Object getItem(int position) {
        return pics[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        ImageView iv = new ImageView(mContext);
//        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        iv.setLayoutParams(mParams);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//            iv.setBackground(mContext.getResources().getDrawable(pics[position]));
//        } else {
//            iv.setBackgroundDrawable(mContext.getResources().getDrawable(pics[position]));
//        }
//        return iv;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.image_item, null);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.imgView);
//        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        iv.setLayoutParams(mParams);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            iv.setBackground(mContext.getResources().getDrawable(pics[position]));
        } else {
            iv.setBackgroundDrawable(mContext.getResources().getDrawable(pics[position]));
        }
        return convertView;
    }
}
